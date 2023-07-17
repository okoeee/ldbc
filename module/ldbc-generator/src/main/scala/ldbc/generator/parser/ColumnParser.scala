/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator.parser

import ldbc.generator.model.*

/** Parser for parsing column definitions.
  *
  * Please refer to the official documentation for MySQL column definitions. SEE:
  * https://dev.mysql.com/doc/refman/8.0/en/create-table.html
  */
trait ColumnParser extends DataTypeParser:

  private def constraint: Parser[String] =
    customErrorWithInput(
      caseSensitivity("not") ~> caseSensitivity("null") ^^ (_ => "NOT NULL") | "NULL",
      input => failureMessage("Nullable", "[NOT] NULL", input)
    )

  private def currentTimestamp: Parser[Default.CurrentTimestamp] =
    customErrorWithInput(
      caseSensitivity("default") ~> caseSensitivity("current_timestamp") ~> opt("(" ~> digit <~ ")") ~
        opt(
          caseSensitivity("on") ~> caseSensitivity("update") ~> caseSensitivity("current_timestamp") ~ opt(
            "(" ~> digit <~ ")"
          )
        ) ^^ {
          case _ ~ Some(attribute ~ _) => Default.CurrentTimestamp(true)
          case _ ~ None                => Default.CurrentTimestamp(false)
        },
      input => failureMessage("default current timestamp", "DEFAULT CURRENT_TIMESTAMP[({0 ~ 6})] [ON UPDATE CURRENT_TIMESTAMP[({0 ~ 6})]]", input)
    )

  private def defaultNull: Parser[Default.Null.type] =
    customErrorWithInput(
      caseSensitivity("default") ~> caseSensitivity("null") ^^ (_ => Default.Null),
      input => failureMessage("default null", "DEFAULT NULL", input)
    )

  private def defaultValue: Parser[Default.Value] =
    customErrorWithInput(
      caseSensitivity("default") ~> (stringLiteral | digit) ^^ Default.Value.apply,
      input => failureMessage("default value", "DEFAULT `value`", input)
    )

  private def default: Parser[Default] = defaultValue | currentTimestamp | defaultNull

  private def visible: Parser[String] =
    caseSensitivity("visible") | caseSensitivity("invisible")

  private def autoInc: Parser[String] =
    caseSensitivity("auto_increment") ^^ (_.toUpperCase)

  protected def primaryKey: Parser[String] =
    customErrorWithInput(
      caseSensitivity("primary") <~ opt(caseSensitivity("key")) ^^ { _ => "PRIMARY_KEY" },
      input => failureMessage("primary key", "PRIMARY [KEY]", input)
    )

  protected def uniqueKey: Parser[String] =
    customErrorWithInput(
      caseSensitivity("unique") <~ opt(caseSensitivity("key")) ^^ { _ => "UNIQUE_KEY" },
      input => failureMessage("unique key", "UNIQUE [KEY]", input)
    )

  protected def columnComment: Parser[Comment] =
    customErrorWithInput(
      caseSensitivity("comment") ~> stringLiteral ^^ Comment.apply,
      input => failureMessage("comment", "COMMENT 'string'", input)
    )

  private def columnFormat: Parser[String] =
    customErrorWithInput(
      caseSensitivity("column_format") ~> (
        caseSensitivity("fixed") | caseSensitivity("dynamic") | caseSensitivity("default")
        ),
      input => failureMessage("column format", "COLUMN_FORMAT {FIXED | DYNAMIC | DEFAULT}", input)
    )

  private def storage: Parser[String] =
    customErrorWithInput(
      caseSensitivity("storage") ~> (caseSensitivity("disk") | caseSensitivity("memory")),
      input => failureMessage("storage", "STORAGE {DISK | MEMORY}", input)
    )

  private def attributes: Parser[Option[Attributes]] =
    opt(constraint) ~ opt(comment) ~ opt(default) ~ opt(comment) ~ opt(visible) ~
      opt(comment) ~ opt(rep(autoInc | primaryKey | uniqueKey)) ~ opt(comment) ~
      opt(columnComment) ~ opt(comment) ~ opt(collate) ~ opt(comment) ~ opt(columnFormat) ~
      opt(comment) ~ opt(engineAttribute) ~ opt(comment) ~ opt(secondaryEngineAttribute) ~ opt(comment) ~
      opt(storage) ^^ {
        case constraint ~ _ ~ default ~ _ ~ visible ~ _ ~ key ~ _ ~ comment ~ _ ~ collate ~ _ ~ columnFormat ~ _ ~ engineAttribute ~ _ ~ secondaryEngineAttribute ~ _ ~ storage =>
          (
            constraint,
            default,
            visible,
            key,
            comment,
            collate,
            columnFormat,
            engineAttribute,
            secondaryEngineAttribute,
            storage
          ) match
            case (None, None, None, None, None, None, None, None, None, None) => None
            case _ =>
              Some(
                Attributes(
                  constraint.forall(_ == "NULL"),
                  default,
                  visible,
                  key,
                  comment,
                  collate,
                  columnFormat,
                  engineAttribute,
                  secondaryEngineAttribute,
                  storage
                )
              )
      }

  protected def columnDefinition: Parser[ColumnDefinition] =
    opt(comment) ~> sqlIdent ~ opt(comment) ~ dataType ~ opt(comment) ~ attributes <~ opt(comment) ^^ {
      case columnName ~ _ ~ dataType ~ _ ~ attributes => ColumnDefinition(columnName, dataType, attributes)
    }
