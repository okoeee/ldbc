/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator

import java.io.File
import java.nio.file.Files

import scala.io.Codec

import ldbc.generator.formatter.Naming
import ldbc.generator.model.*

/** An object for generating a model about Table.
  */
private[ldbc] object TableModelGenerator:

  /** Methods for generating models.
    *
    * @param database
    *   Name of the database in which the Table is stored.
    * @param statement
    *   Model generated by parsing a Create table statement.
    * @param classNameFormatter
    *   Value for formatting Class name.
    * @param propertyNameFormatter
    *   Value for formatting Property name.
    * @param sourceManaged
    *   The file to which the model will be generated.
    * @return
    *   A file containing the generated table model.
    */
  def generate(
    database:              String,
    statement:             Table.CreateStatement,
    classNameFormatter:    Naming,
    propertyNameFormatter: Naming,
    sourceManaged:         File
  ): File =
    val className = classNameFormatter.format(statement.tableName)
    val properties = statement.columnDefinitions.map(column =>
      propertyGenerator(className, column, propertyNameFormatter, classNameFormatter)
    )

    val objects =
      statement.columnDefinitions.map(column => enumGenerator(column, classNameFormatter)).filter(_.nonEmpty)

    val directory = sourceManaged.toPath.resolve(database)
    val output = if !directory.toFile.exists() then
      directory.toFile.getParentFile.mkdirs()
      Files.createDirectory(directory)
    else directory

    val outputFile = new File(output.toFile, s"$className.scala")

    if !outputFile.exists() then
      outputFile.getParentFile.mkdirs()
      outputFile.createNewFile()

    val keyDefinitions = statement.keyDefinitions.map(key =>
      s".keySet(table => ${ key.toCode("table", classNameFormatter, propertyNameFormatter) })"
    )

    val packageName = if database.nonEmpty then s"ldbc.generated.$database" else "ldbc.generated"

    val columns =
      statement.columnDefinitions.map((column: ColumnDefinition) =>
        column.dataType.scalaType match
          case ScalaType.Enum(types) => column.copy(name = classNameFormatter.format(column.name))
          case _                     => column
      )

    val scalaSource =
      s"""
         |package $packageName
         |
         |import ldbc.core.*
         |
         |case class $className(
         |  ${ properties.mkString(",\n  ") }
         |)
         |
         |object $className:
         |
         |  ${ objects.mkString("\n  ") }
         |  val table: TABLE[$className] = Table[$className]("${ statement.tableName }")(
         |    ${ columns.map(_.toCode).mkString(",\n    ") }
         |  )
         |  ${ keyDefinitions.mkString("\n  ") }
         |""".stripMargin

    Files.write(outputFile.toPath, scalaSource.getBytes(summon[Codec].name))
    outputFile

  private def propertyGenerator(
    className:             String,
    column:                ColumnDefinition,
    propertyNameFormatter: Naming,
    classNameFormatter:    Naming
  ): String =

    val name = propertyNameFormatter.format(column.name)

    val isOptional = column.attributes.forall(_.constraint)

    // (column.attributes.forall(_.constraint), column.dataType.scalaType) match
    //  case (true, _: ScalaType.Enum)  => s"$name: Option[$className.${ classNameFormatter.format(column.name) }]"
    //  case (false, _: ScalaType.Enum) => s"$name: $className.${ classNameFormatter.format(column.name) }"
    //  case (true, _)                  => s"$name: Option[${ column.dataType.scalaType.code }]"
    //  case (false, _)                 => s"$name: ${ column.dataType.scalaType.code }"
    column.dataType.scalaType match
      case _: ScalaType.Enum =>
        if isOptional then s"$name: Option[$className.${ classNameFormatter.format(column.name) }]"
        else s"$name: $className.${ classNameFormatter.format(column.name) }"
      case _ => s"$name: ${ column.dataType.propertyType(isOptional) }"

  private def enumGenerator(column: ColumnDefinition, formatter: Naming): String =
    column.dataType.scalaType match
      case ScalaType.Enum(types) =>
        val enumName = formatter.format(column.name)
        s"""enum $enumName extends ldbc.core.model.Enum:
           |    case ${ types.mkString(", ") }
           |  object $enumName extends ldbc.core.model.EnumDataType[$enumName]
           |""".stripMargin
      case _ => ""
