/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector

import cats.effect.*

import munit.CatsEffectSuite

import org.typelevel.otel4s.trace.Tracer

class CallableStatementTest extends CatsEffectSuite:

  given Tracer[IO] = Tracer.noop[IO]

  private val connection = Connection[IO](
    host                    = "127.0.0.1",
    port                    = 13306,
    user                    = "ldbc",
    password                = Some("password"),
    database                = Some("connector_test"),
    allowPublicKeyRetrieval = true
    // ssl = SSL.Trusted
  )

  test("The result of calling an empty procedure matches the specified value.") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL proc1()")
          resultSet         <- callableStatement.executeQuery()
          value             <- resultSet.getString(1)
        yield value
      },
      Some("8.0.33")
    )
  }

  test("The result of calling a procedure that accepts only one IN parameter argument matches the specified value.") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL proc2(?)")
          resultSet         <- callableStatement.setInt(1, 1024) *> callableStatement.executeQuery()
          value             <- resultSet.getInt(1)
        yield value
      },
      1024
    )
  }

  test(
    "The result of calling a procedure that accepts one or more IN parameter arguments matches the specified value."
  ) {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL proc3(?, ?)")
          resultSet <- callableStatement.setInt(1, 1024) *> callableStatement.setString(2, "Hello") *> callableStatement
                         .executeQuery()
          param1 <- resultSet.getInt(1)
          param2 <- resultSet.getString(2)
        yield (param1, param2)
      },
      (1024, Some("Hello"))
    )
  }

  test(
    "The result of calling a procedure that accepts one or more OUT parameter arguments matches the specified value."
  ) {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL proc4(?, ?)")
          _      <- callableStatement.setInt(1, 1) *> callableStatement.setInt(2, 2) *> callableStatement.executeQuery()
          param1 <- callableStatement.getInt(1)
          param2 <- callableStatement.getString(2)
        yield (param1, param2)
      },
      (-1, Some("hello"))
    )
  }

  test("") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL demoSp(?, ?)")
          resultSet <- callableStatement.setString(1, "abcdefg") *> callableStatement.setInt(2, 1) *> callableStatement
                         .executeQuery()
          value <- resultSet.getString(1)
        yield value
      },
      Some("abcdefg")
    )
  }

  test("") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL demoSp(?, ?)")
          resultSet <- callableStatement.setString(1, "abcdefg") *> callableStatement.setInt(2, 1) *> callableStatement
                         .executeQuery()
          outParam <- callableStatement.getInt(2)
        yield outParam
      },
      2
    )
  }

  test("") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("CALL demoSp(?, ?)")
          resultSet <- callableStatement.setString(1, "abcdefg") *> callableStatement.setInt(2, 1) *> callableStatement
                         .executeQuery()
          outParam <- callableStatement.getInt("inOutParam")
        yield outParam
      },
      2
    )
  }

  test("The result of calling a stored function with an empty parameter argument matches the specified value.") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("SELECT func1()")
          resultSet         <- callableStatement.executeQuery()
          value             <- resultSet.getInt(1)
        yield value
      },
      -1
    )
  }

  test("The result of calling a stored function with an empty parameter argument matches the specified value.") {
    assertIO(
      connection.use { conn =>
        for
          callableStatement <- conn.prepareCall("SELECT func2()")
          resultSet         <- callableStatement.executeQuery()
          value             <- resultSet.getString(1)
        yield value
      },
      Some("hello, world")
    )
  }
