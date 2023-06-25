/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator

import ldbc.generator.model.*

trait TableParser extends KeyParser:

  private def temporary: Parser[String] = caseSensitivity("temporary") ^^ (_.toUpperCase)
  private def table:     Parser[String] = caseSensitivity("table") ^^ (_.toUpperCase)

  protected def tableStatements: Parser[Table.CreateStatement | Table.DropStatement] = createStatement | dropStatement

  private def createStatement: Parser[Table.CreateStatement] =
    opt(comment) ~> create ~> opt(comment) ~> opt(temporary) ~> opt(comment) ~> table ~>
      opt(comment) ~> opt(ifNotExists) ~> opt(comment) ~> sqlIdent ~ opt(comment) ~
      "(" ~ repsep(columnDefinition | keyDefinitions, ",") ~ opt(comment) <~ ")" ~ ";" ^^ {
        case tableName ~ _ ~ _ ~ objects ~ _ =>
          val columnDefs = objects.filter(_.isInstanceOf[ColumnDefinition]).asInstanceOf[List[ColumnDefinition]]
          val keyDefs    = objects.filter(_.isInstanceOf[Key]).asInstanceOf[List[Key]]
          Table.CreateStatement(tableName, columnDefs, keyDefs)
      }

  private def dropStatement: Parser[Table.DropStatement] =
    opt(comment) ~> drop ~> opt(comment) ~> opt(ifNotExists) ~> opt(comment) ~> sqlIdent <~ opt(comment) <~ ";" ^^ {
      tableName => Table.DropStatement(tableName)
    }
