/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.dsl.internal

import cats.implicits.*

import cats.effect.Sync

import ldbc.sql.{ ResultSet, Statement }

trait StatementSyntax extends ResultSetSyntax:

  implicit class StatementF(statementObject: Statement.type):

    def apply[F[_]: Sync](statement: java.sql.Statement): Statement[F] = new Statement[F]:

      override def executeQuery(sql: String): F[ResultSet[F]] =
        Sync[F].blocking(statement.executeQuery(sql)).map(ResultSet[F])

      override def executeUpdate(sql: String): F[Int] = Sync[F].blocking(statement.executeUpdate(sql))

      override def close(): F[Unit] = Sync[F].blocking(statement.close())

      override def execute(sql: String): F[Boolean] = Sync[F].blocking(statement.execute(sql))

      override def getResultSet(): F[Option[ResultSet[F]]] =
        Sync[F].blocking(Option(statement.getResultSet)).map(_.map(ResultSet[F]))

      override def getUpdateCount(): F[Int] = Sync[F].blocking(statement.getUpdateCount)

      override def getMoreResults(): F[Boolean] = Sync[F].blocking(statement.getMoreResults)

      override def addBatch(sql: String): F[Unit] = Sync[F].blocking(statement.addBatch(sql))

      override def clearBatch(): F[Unit] = Sync[F].blocking(statement.clearBatch())

      override def executeBatch(): F[Array[Int]] = Sync[F].blocking(statement.executeBatch())

      override def getGeneratedKeys(): F[ResultSet[F]] =
        Sync[F].blocking(statement.getGeneratedKeys).map(ResultSet[F])

      override def executeUpdate(sql: String, autoGeneratedKeys: Int): F[Int] =
        Sync[F].blocking(statement.executeUpdate(sql, autoGeneratedKeys))

      override def execute(sql: String, autoGeneratedKeys: Int): F[Boolean] =
        Sync[F].blocking(statement.execute(sql, autoGeneratedKeys))

      override def isClosed(): F[Boolean] = Sync[F].blocking(statement.isClosed)
