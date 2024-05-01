/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector

import org.typelevel.otel4s.trace.Tracer

import com.comcast.ip4s.UnknownHostException

import cats.Monad

import cats.effect.*

import munit.CatsEffectSuite

import ldbc.connector.exception.*

class ConnectionTest extends CatsEffectSuite:

  given Tracer[IO] = Tracer.noop[IO]

  test("Passing an empty string to host causes SQLException") {
    val connection = Connection[IO](
      host = "",
      port = 13306,
      user = "root"
    )
    interceptIO[SQLClientInfoException] {
      connection.use(_ => IO.unit)
    }
  }

  test("UnknownHostException occurs when invalid host is passed") {
    val connection = Connection[IO](
      host = "host",
      port = 13306,
      user = "root"
    )
    interceptIO[UnknownHostException] {
      connection.use(_ => IO.unit)
    }
  }

  test("Passing a negative value to Port causes SQLException") {
    val connection = Connection[IO](
      host = "127.0.0.1",
      port = -1,
      user = "root"
    )
    interceptIO[SQLClientInfoException] {
      connection.use(_ => IO.unit)
    }
  }

  test("SQLException occurs when passing more than 65535 values to Port") {
    val connection = Connection[IO](
      host = "127.0.0.1",
      port = 65536,
      user = "root"
    )
    interceptIO[SQLClientInfoException] {
      connection.use(_ => IO.unit)
    }
  }

  test("A user using mysql_native_password can establish a connection with the MySQL server.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password")
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "Connections to MySQL servers using users with mysql_native_password will succeed if allowPublicKeyRetrieval is enabled for non-SSL connections."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc_mysql_native_user",
      password                = Some("ldbc_mysql_native_password"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Connections to MySQL servers using users with mysql_native_password will succeed for SSL connections.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Users using mysql_native_password can establish a connection with the MySQL server by specifying database.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password"),
      database = Some("connector_test")
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "If allowPublicKeyRetrieval is enabled for non-SSL connections, a connection to a MySQL server specifying a database using a user with mysql_native_password will succeed."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc_mysql_native_user",
      password                = Some("ldbc_mysql_native_password"),
      database                = Some("connector_test"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "A connection to a MySQL server with a database specified using a user with mysql_native_password will succeed with an SSL connection."
  ) {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Connections to MySQL servers using users with sha256_password will fail for non-SSL connections.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_sha256_user",
      password = Some("ldbc_sha256_password")
    )
    interceptIO[SQLInvalidAuthorizationSpecException] {
      connection.use(_ => IO.unit)
    }
  }

  test(
    "Connections to MySQL servers using users with sha256_password will succeed if allowPublicKeyRetrieval is enabled for non-SSL connections."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc_sha256_user",
      password                = Some("ldbc_sha256_password"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Connections to MySQL servers using users with sha256_password will succeed for SSL connections.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_sha256_user",
      password = Some("ldbc_sha256_password"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "If allowPublicKeyRetrieval is enabled for non-SSL connections, a connection to a MySQL server specifying a database using a user with sha256_password will succeed."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc_sha256_user",
      password                = Some("ldbc_sha256_password"),
      database                = Some("connector_test"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "A connection to a MySQL server with a database specified using a user with sha256_password will succeed with an SSL connection."
  ) {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_sha256_user",
      password = Some("ldbc_sha256_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "Connections to MySQL servers using users with caching_sha2_password will succeed if allowPublicKeyRetrieval is enabled for non-SSL connections."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc",
      password                = Some("password"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Connections to MySQL servers using users with caching_sha2_password will succeed for SSL connections.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "If the login information of a user using caching_sha2_password is cached, the connection to the MySQL server will succeed even for non-SSL connections."
  ) {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password")
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "If allowPublicKeyRetrieval is enabled for non-SSL connections, a connection to a MySQL server specifying a database using a user with caching_sha2_password will succeed."
  ) {
    val connection = Connection[IO](
      host                    = "127.0.0.1",
      port                    = 13306,
      user                    = "ldbc",
      password                = Some("password"),
      database                = Some("connector_test"),
      allowPublicKeyRetrieval = true
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test(
    "A connection to a MySQL server with a database specified using a user with caching_sha2_password will succeed with an SSL connection."
  ) {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )
    assertIOBoolean(connection.use(_ => IO(true)))
  }

  test("Schema change will change the currently connected Schema.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use { conn =>
        conn.setSchema("world") *> conn.getSchema
      },
      "world"
    )
  }

  test("Statistics of the MySQL server can be obtained.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getStatistics.map(_.toString)),
      "COM_STATISTICS Response Packet"
    )
  }

  test("The connection is valid.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.isValid))
  }

  test("Connection state reset succeeds.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.resetServerState) *> IO(true))
  }

  test("If multi-querying is not enabled, ERRPacketException is raised when multi-querying is performed.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    interceptMessageIO[SQLSyntaxErrorException](
      List(
        "Message: Failed to execute query",
        "SQLState: 42000",
        "Vendor Code: 1064",
        "SQL: SELECT 1; SELECT2",
        "Detail: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'SELECT2' at line 1"
      ).mkString(", ")
    )(connection.use { conn =>
      for
        statement <- conn.createStatement()
        resultSet <- statement.executeQuery("SELECT 1; SELECT2")
      yield resultSet
    })
  }

  test("Can change from mysql_native_password user to caching_sha2_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc", "password")) *> IO.pure(true),
      true
    )
  }

  test("Can change from mysql_native_password user to sha256_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_mysql_native_user",
      password = Some("ldbc_mysql_native_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc_sha256_user", "ldbc_sha256_password")) *> IO.pure(true),
      true
    )
  }

  test("Can change from sha256_password user to mysql_native_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_sha256_user",
      password = Some("ldbc_sha256_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc_mysql_native_user", "ldbc_mysql_native_password")) *> IO.pure(true),
      true
    )
  }

  test("Can change from sha256_password user to caching_sha2_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc_sha256_user",
      password = Some("ldbc_sha256_password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc", "password")) *> IO.pure(true),
      true
    )
  }

  test("Can change from caching_sha2_password user to mysql_native_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc_mysql_native_user", "ldbc_mysql_native_password")) *> IO.pure(true),
      true
    )
  }

  test("Can change from caching_sha2_password user to sha256_password user.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(
      connection.use(_.changeUser("ldbc_sha256_user", "ldbc_sha256_password")) *> IO.pure(true),
      true
    )
  }

  test("The allProceduresAreCallable method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.allProceduresAreCallable())))
  }

  test("The allTablesAreSelectable method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.allTablesAreSelectable())))
  }

  test("The URL retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getURL())),
      "jdbc:mysql://127.0.0.1:13306/connector_test"
    )
  }

  test("The User name retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().flatMap(_.getUserName())),
      "ldbc@172.18.0.1"
    )
  }

  test("The isReadOnly method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.isReadOnly())))
  }

  test("The nullsAreSortedHigh method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.nullsAreSortedHigh())))
  }

  test("The nullsAreSortedLow method of DatabaseMetaData is always true.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => meta.nullsAreSortedLow())))
  }

  test("The nullsAreSortedAtStart method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.nullsAreSortedAtStart())))
  }

  test("The nullsAreSortedAtEnd method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.nullsAreSortedAtEnd())))
  }

  test("The getDatabaseProductName method of DatabaseMetaData is always MySQL.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getDatabaseProductName())),
      "MySQL"
    )
  }

  test("The Server version retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getDatabaseProductVersion())),
      "8.0.33"
    )
  }

  test("The getDriverName method of DatabaseMetaData is always MySQL Connector/L.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getDriverName())),
      "MySQL Connector/L"
    )
  }

  test("The Driver version retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getDriverVersion())),
      "ldbc-connector-0.3.0"
    )
  }

  test("The usesLocalFiles method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.usesLocalFiles())))
  }

  test("The usesLocalFilePerTable method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.usesLocalFilePerTable())))
  }

  test("The supports Mixed Case Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(_.supportsMixedCaseIdentifiers())))
  }

  test("The storesUpperCaseIdentifiers method of DatabaseMetaData is always false.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.storesUpperCaseIdentifiers())))
  }

  test("The stores Lower Case Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.storesLowerCaseIdentifiers())))
  }

  test("The stores Mixed Case Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(_.storesMixedCaseIdentifiers())))
  }

  test("The supports Mixed Case Quoted Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(_.supportsMixedCaseQuotedIdentifiers())))
  }

  test("The storesUpperCaseQuotedIdentifiers method of DatabaseMetaData is always true.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(_.storesUpperCaseQuotedIdentifiers())))
  }

  test("The stores Lower Case Quoted Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(meta => !meta.storesLowerCaseQuotedIdentifiers())))
  }

  test("The stores Mixed Case Quoted Identifiers retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIOBoolean(connection.use(_.getMetaData().map(_.storesMixedCaseQuotedIdentifiers())))
  }

  test("The Identifier Quote String retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getIdentifierQuoteString())),
      "`"
    )
  }

  test("The SQL Keywords retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().flatMap(_.getSQLKeywords())),
      "ACCESSIBLE,ADD,ANALYZE,ASC,BEFORE,CASCADE,CHANGE,CONTINUE,DATABASE,DATABASES,DAY_HOUR,DAY_MICROSECOND,DAY_MINUTE,DAY_SECOND,DELAYED,DESC,DISTINCTROW,DIV,DUAL,ELSEIF,EMPTY,ENCLOSED,ESCAPED,EXIT,EXPLAIN,FIRST_VALUE,FLOAT4,FLOAT8,FORCE,FULLTEXT,GENERATED,GROUPS,HIGH_PRIORITY,HOUR_MICROSECOND,HOUR_MINUTE,HOUR_SECOND,IF,IGNORE,INDEX,INFILE,INT1,INT2,INT3,INT4,INT8,IO_AFTER_GTIDS,IO_BEFORE_GTIDS,ITERATE,JSON_TABLE,KEY,KEYS,KILL,LAG,LAST_VALUE,LEAD,LEAVE,LIMIT,LINEAR,LINES,LOAD,LOCK,LONG,LONGBLOB,LONGTEXT,LOOP,LOW_PRIORITY,MASTER_BIND,MASTER_SSL_VERIFY_SERVER_CERT,MAXVALUE,MEDIUMBLOB,MEDIUMINT,MEDIUMTEXT,MIDDLEINT,MINUTE_MICROSECOND,MINUTE_SECOND,NO_WRITE_TO_BINLOG,NTH_VALUE,NTILE,OPTIMIZE,OPTIMIZER_COSTS,OPTION,OPTIONALLY,OUTFILE,PURGE,READ,READ_WRITE,REGEXP,RENAME,REPEAT,REPLACE,REQUIRE,RESIGNAL,RESTRICT,RLIKE,SCHEMA,SCHEMAS,SECOND_MICROSECOND,SEPARATOR,SHOW,SIGNAL,SPATIAL,SQL_BIG_RESULT,SQL_CALC_FOUND_ROWS,SQL_SMALL_RESULT,SSL,STARTING,STORED,STRAIGHT_JOIN,TERMINATED,TINYBLOB,TINYINT,TINYTEXT,UNDO,UNLOCK,UNSIGNED,USAGE,USE,UTC_DATE,UTC_TIME,UTC_TIMESTAMP,VARBINARY,VARCHARACTER,VIRTUAL,WHILE,WRITE,XOR,YEAR_MONTH,ZEROFILL"
    )
  }

  test("The Numeric Functions retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getNumericFunctions())),
      "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE"
    )
  }

  test("The String Functions retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getStringFunctions())),
      "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,"
        + "INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,"
        + "QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,"
        + "SUBSTRING_INDEX,TRIM,UCASE,UPPER"
    )
  }

  test("The System Functions retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getSystemFunctions())),
      "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION"
    )
  }

  test("The Time Date Functions retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getTimeDateFunctions())),
      "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,"
        + "PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,"
        + "CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC"
    )
  }

  test("The Search String Escape retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getSearchStringEscape())),
      "\\"
    )
  }

  test("The Extra Name Characters retrieved from DatabaseMetaData matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use(_.getMetaData().map(_.getExtraNameCharacters())),
      "#@"
    )
  }

  test("The result of retrieving procedure information matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getProcedures("def", "connector_test", "demoSp")
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        procedureCat   <- resultSet.getString("PROCEDURE_CAT")
                        procedureSchem <- resultSet.getString("PROCEDURE_SCHEM")
                        procedureName  <- resultSet.getString("PROCEDURE_NAME")
                        remarks        <- resultSet.getString("REMARKS")
                        procedureType  <- resultSet.getString("PROCEDURE_TYPE")
                      yield s"Procedure Catalog: $procedureCat, Procedure Schema: $procedureSchem, Procedure Name: $procedureName, Remarks: $remarks, Procedure Type: $procedureType"
                    }
        yield values
      },
      Vector(
        "Procedure Catalog: Some(connector_test), Procedure Schema: None, Procedure Name: Some(demoSp), Remarks: Some(), Procedure Type: Some(1)"
      )
    )
  }

  test("The result of retrieving procedure columns information matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getProcedureColumns(Some("def"), Some("connector_test"), Some("demoSp"), None)
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        procedureCat   <- resultSet.getString("PROCEDURE_CAT")
                        procedureSchem <- resultSet.getString("PROCEDURE_SCHEM")
                        procedureName  <- resultSet.getString("PROCEDURE_NAME")
                        columnName     <- resultSet.getString("COLUMN_NAME")
                        columnType     <- resultSet.getString("COLUMN_TYPE")
                      yield s"Procedure Catalog: $procedureCat, Procedure Schema: $procedureSchem, Procedure Name: $procedureName, Column Name: $columnName, Column Type: $columnType"
                    }
        yield values
      },
      Vector(
        "Procedure Catalog: Some(connector_test), Procedure Schema: None, Procedure Name: Some(demoSp), Column Name: Some(inputParam), Column Type: Some(1)",
        "Procedure Catalog: Some(connector_test), Procedure Schema: None, Procedure Name: Some(demoSp), Column Name: Some(inOutParam), Column Type: Some(2)"
      )
    )
  }

  test("The result of retrieving tables information matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      // ssl      = SSL.Trusted
      allowPublicKeyRetrieval = true
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getTables(None, Some("connector_test"), Some("all_types"), Array.empty[String])
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        tableCat               <- resultSet.getString("TABLE_CAT")
                        tableSchem             <- resultSet.getString("TABLE_SCHEM")
                        tableName              <- resultSet.getString("TABLE_NAME")
                        tableType              <- resultSet.getString("TABLE_TYPE")
                        remarks                <- resultSet.getString("REMARKS")
                        typeCat                <- resultSet.getString("TYPE_CAT")
                        typeSchem              <- resultSet.getString("TYPE_SCHEM")
                        typeName               <- resultSet.getString("TYPE_NAME")
                        selfReferencingColName <- resultSet.getString("SELF_REFERENCING_COL_NAME")
                        refGeneration          <- resultSet.getString("REF_GENERATION")
                      yield s"Table Catalog: $tableCat, Table Schema: $tableSchem, Table Name: $tableName, Table Type: $tableType, Remarks: $remarks, Type Catalog: $typeCat, Type Schema: $typeSchem, Type Name: $typeName, Self Referencing Column Name: $selfReferencingColName, Reference Generation: $refGeneration"
                    }
        yield values
      },
      Vector(
        "Table Catalog: Some(connector_test), Table Schema: None, Table Name: Some(all_types), Table Type: Some(TABLE), Remarks: Some(), Type Catalog: None, Type Schema: None, Type Name: None, Self Referencing Column Name: None, Reference Generation: None"
      )
    )
  }

  test("The result of retrieving schemas information matches the specified value.") {
    val connection = Connection[IO](
      host         = "127.0.0.1",
      port         = 13306,
      user         = "ldbc",
      password     = Some("password"),
      database     = Some("connector_test"),
      ssl          = SSL.Trusted,
      databaseTerm = Some(DatabaseMetaData.DatabaseTerm.SCHEMA)
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getSchemas()
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        tableCatalog <- resultSet.getString("TABLE_CATALOG")
                        tableSchem   <- resultSet.getString("TABLE_SCHEM")
                      yield s"Table Catalog: $tableCatalog, Table Schema: $tableSchem"
                    }
        yield values
      },
      Vector(
        "Table Catalog: Some(def), Table Schema: Some(connector_test)",
        "Table Catalog: Some(def), Table Schema: Some(information_schema)",
        "Table Catalog: Some(def), Table Schema: Some(mysql)",
        "Table Catalog: Some(def), Table Schema: Some(performance_schema)",
        "Table Catalog: Some(def), Table Schema: Some(sys)",
        "Table Catalog: Some(def), Table Schema: Some(world)",
        "Table Catalog: Some(def), Table Schema: Some(world2)"
      )
    )
  }

  test("The result of retrieving catalogs information matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getCatalogs()
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for tableCatalog <- resultSet.getString("TABLE_CAT")
                      yield s"Table Catalog: $tableCatalog"
                    }
        yield values
      },
      Vector(
        "Table Catalog: Some(connector_test)",
        "Table Catalog: Some(information_schema)",
        "Table Catalog: Some(mysql)",
        "Table Catalog: Some(performance_schema)",
        "Table Catalog: Some(sys)",
        "Table Catalog: Some(world)",
        "Table Catalog: Some(world2)"
      )
    )
  }

  test("The result of retrieving tableTypes information matches the specified value.") {
    val connection = Connection[IO](
      host     = "127.0.0.1",
      port     = 13306,
      user     = "ldbc",
      password = Some("password"),
      database = Some("connector_test"),
      ssl      = SSL.Trusted
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getTableTypes()
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for tableType <- resultSet.getString("TABLE_TYPE")
                      yield s"Table Type: $tableType"
                    }
        yield values
      },
      Vector(
        "Table Type: Some(LOCAL TEMPORARY)",
        "Table Type: Some(SYSTEM TABLE)",
        "Table Type: Some(SYSTEM VIEW)",
        "Table Type: Some(TABLE)",
        "Table Type: Some(VIEW)"
      )
    )
  }

  test("The result of retrieving columns information matches the specified value.") {
    val connection = Connection[IO](
      host         = "127.0.0.1",
      port         = 13306,
      user         = "ldbc",
      password     = Some("password"),
      database     = Some("connector_test"),
      ssl          = SSL.Trusted,
      databaseTerm = Some(DatabaseMetaData.DatabaseTerm.SCHEMA)
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getColumns(None, None, Some("privileges_table"), None)
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        tableCat          <- resultSet.getString("TABLE_CAT")
                        tableSchem        <- resultSet.getString("TABLE_SCHEM")
                        tableName         <- resultSet.getString("TABLE_NAME")
                        columnName        <- resultSet.getString("COLUMN_NAME")
                        dataType          <- resultSet.getInt("DATA_TYPE")
                        typeName          <- resultSet.getString("TYPE_NAME")
                        columnSize        <- resultSet.getInt("COLUMN_SIZE")
                        bufferLength      <- resultSet.getInt("BUFFER_LENGTH")
                        decimalDigits     <- resultSet.getInt("DECIMAL_DIGITS")
                        numPrecRadix      <- resultSet.getInt("NUM_PREC_RADIX")
                        nullable          <- resultSet.getInt("NULLABLE")
                        remarks           <- resultSet.getString("REMARKS")
                        columnDef         <- resultSet.getString("COLUMN_DEF")
                        sqlDataType       <- resultSet.getInt("SQL_DATA_TYPE")
                        sqlDatetimeSub    <- resultSet.getInt("SQL_DATETIME_SUB")
                        charOctetLength   <- resultSet.getInt("CHAR_OCTET_LENGTH")
                        ordinalPosition   <- resultSet.getInt("ORDINAL_POSITION")
                        isNullable        <- resultSet.getString("IS_NULLABLE")
                        scopeCatalog      <- resultSet.getString("SCOPE_CATALOG")
                        scopeSchema       <- resultSet.getString("SCOPE_SCHEMA")
                        scopeTable        <- resultSet.getString("SCOPE_TABLE")
                        sourceDataType    <- resultSet.getShort("SOURCE_DATA_TYPE")
                        isAutoincrement   <- resultSet.getString("IS_AUTOINCREMENT")
                        isGeneratedcolumn <- resultSet.getString("IS_GENERATEDCOLUMN")
                      yield s"Table Cat: $tableCat, Table Schem: $tableSchem, Table Name: $tableName, Column Name: $columnName, Data Type: $dataType, Type Name: $typeName, Column Size: $columnSize, Buffer Length: $bufferLength, Decimal Digits: $decimalDigits, Num Prec Radix: $numPrecRadix, Nullable: $nullable, Remarks: $remarks, Column Def: $columnDef, SQL Data Type: $sqlDataType, SQL Datetime Sub: $sqlDatetimeSub, Char Octet Length: $charOctetLength, Ordinal Position: $ordinalPosition, Is Nullable: $isNullable, Scope Catalog: $scopeCatalog, Scope Schema: $scopeSchema, Scope Table: $scopeTable, Source Data Type: $sourceDataType, Is Autoincrement: $isAutoincrement, Is Generatedcolumn: $isGeneratedcolumn"
                    }
        yield values
      },
      Vector(
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c1), Data Type: 4, Type Name: Some(INT), Column Size: 10, Buffer Length: 65535, Decimal Digits: 0, Num Prec Radix: 10, Nullable: 0, Remarks: Some(), Column Def: None, SQL Data Type: 0, SQL Datetime Sub: 0, Char Octet Length: 0, Ordinal Position: 1, Is Nullable: Some(NO), Scope Catalog: None, Scope Schema: None, Scope Table: None, Source Data Type: 0, Is Autoincrement: Some(NO), Is Generatedcolumn: Some(NO)",
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c2), Data Type: 4, Type Name: Some(INT), Column Size: 10, Buffer Length: 65535, Decimal Digits: 0, Num Prec Radix: 10, Nullable: 0, Remarks: Some(), Column Def: None, SQL Data Type: 0, SQL Datetime Sub: 0, Char Octet Length: 0, Ordinal Position: 2, Is Nullable: Some(NO), Scope Catalog: None, Scope Schema: None, Scope Table: None, Source Data Type: 0, Is Autoincrement: Some(NO), Is Generatedcolumn: Some(NO)"
      )
    )
  }

  test("The result of retrieving column privileges information matches the specified value.") {
    val connection = Connection[IO](
      host         = "127.0.0.1",
      port         = 13306,
      user         = "ldbc",
      password     = Some("password"),
      database     = Some("connector_test"),
      ssl          = SSL.Trusted,
      databaseTerm = Some(DatabaseMetaData.DatabaseTerm.SCHEMA)
    )

    assertIO(
      connection.use { conn =>
        for
          metaData  <- conn.getMetaData()
          resultSet <- metaData.getColumnPrivileges(None, Some("connector_test"), Some("privileges_table"), None)
          values <- Monad[IO].whileM[Vector, String](resultSet.next()) {
                      for
                        tableCat    <- resultSet.getString("TABLE_CAT")
                        tableSchem  <- resultSet.getString("TABLE_SCHEM")
                        tableName   <- resultSet.getString("TABLE_NAME")
                        columnName  <- resultSet.getString("COLUMN_NAME")
                        grantor     <- resultSet.getString("GRANTOR")
                        grantee     <- resultSet.getString("GRANTEE")
                        privilege   <- resultSet.getString("PRIVILEGE")
                        isGrantable <- resultSet.getString("IS_GRANTABLE")
                      yield s"Table Cat: $tableCat, Table Schem: $tableSchem, Table Name: $tableName, Column Name: $columnName, Grantor: $grantor, Grantee: $grantee, Privilege: $privilege, Is Grantable: $isGrantable"
                    }
        yield values
      },
      Vector(
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c1), Grantor: None, Grantee: Some('ldbc'@'%'), Privilege: Some(INSERT), Is Grantable: Some(NO)",
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c1), Grantor: None, Grantee: Some('ldbc'@'%'), Privilege: Some(SELECT), Is Grantable: Some(NO)",
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c2), Grantor: None, Grantee: Some('ldbc'@'%'), Privilege: Some(INSERT), Is Grantable: Some(NO)",
        "Table Cat: Some(def), Table Schem: Some(connector_test), Table Name: Some(privileges_table), Column Name: Some(c2), Grantor: None, Grantee: Some('ldbc'@'%'), Privilege: Some(SELECT), Is Grantable: Some(NO)"
      )
    )
  }
