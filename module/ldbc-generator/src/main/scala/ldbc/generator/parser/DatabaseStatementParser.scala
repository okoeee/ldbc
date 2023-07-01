/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator.parser

import ldbc.generator.model.*

/** Parser for parsing Database definitions.
  */
trait DatabaseStatementParser extends TableParser:

  /** Parser for parsing Database create statement.
    *
    * Please refer to the official documentation for MySQL Database create statement. SEE:
    * https://dev.mysql.com/doc/refman/8.0/en/create-database.html
    */
  private def createStatement: Parser[Database.CreateStatement] =
    opt(comment) ~> create ~> opt(comment) ~> (caseSensitivity("database") | caseSensitivity("schema")) ~>
      opt(comment) ~> opt(ifNotExists) ~> opt(comment) ~> sqlIdent ~ opt(comment) ~ opt(caseSensitivity("default")) ~
      opt(comment) ~ opt(character ~ opt(collate)) ~ opt(comment) ~ opt(encryption) <~
      opt(comment) <~ ";" ^^ {
        case name ~ _ ~ _ ~ _ ~ Some(character ~ collate) ~ _ ~ encryption =>
          Database.CreateStatement(name, Some(character), collate, encryption.map(_.value))
        case name ~ _ ~ _ ~ _ ~ None ~ _ ~ encryption =>
          Database.CreateStatement(name, None, None, encryption.map(_.value))
      }

  /** Parser for parsing Database drop statement.
    *
    * Please refer to the official documentation for MySQL Database drop statement. SEE:
    * https://dev.mysql.com/doc/refman/8.0/en/drop-database.html
    */
  private[ldbc] def dropStatement: Parser[Database.DropStatement] =
    customError(
      opt(comment) ~> drop ~> opt(comment) ~> (caseSensitivity("database") | caseSensitivity("schema")) ~>
        opt(comment) ~> opt(ifExists) ~> opt(comment) ~> sqlIdent <~ ";" ^^ { name =>
          Database.DropStatement(name)
        },
      """
        |======================================================
        |There is an error in the if drop database statement format.
        |Please correct the format according to the following.
        |
        |example: DROP {DATABASE | SCHEMA} [IF EXISTS] `database_name`
        |======================================================
        |""".stripMargin
    )

  /** Parser for parsing Database use statement.
    */
  private def useDatabase: Parser[Database.DropStatement] =
    customError(
      opt(comment) ~> caseSensitivity("use") ~> opt(comment) ~> sqlIdent <~ opt(comment) <~ ";" ^^ { name =>
        Database.DropStatement(name)
      },
      """
        |======================================================
        |There is an error in the if use database statement format.
        |Please correct the format according to the following.
        |
        |example: USE `database_name`
        |======================================================
        |""".stripMargin
    )

  protected def databaseStatement: Parser[Database.CreateStatement | Database.DropStatement | Database.UseStatement] =
    createStatement | dropStatement | useDatabase
