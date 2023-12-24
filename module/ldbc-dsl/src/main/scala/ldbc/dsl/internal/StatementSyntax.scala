/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
 * distributed with this source code.
 */

package ldbc.dsl.internal

import java.sql.SQLWarning

import cats.implicits.*

import cats.effect.Sync

import ldbc.sql.{ ResultSet, Statement }
import ldbc.dsl.ResultSetIO

transparent trait StatementSyntax:
  
  extension (statementObject: Statement.type)

    def apply[F[_] : Sync](statement: java.sql.Statement): Statement[F] = new Statement[F]:
  
      override def executeQuery(sql: String): F[ResultSet[F]] =
        Sync[F].blocking(statement.executeQuery(sql)).map(ResultSetIO[F])
  
      override def executeUpdate(sql: String): F[Int] = Sync[F].blocking(statement.executeUpdate(sql))
  
      override def close(): F[Unit] = Sync[F].blocking(statement.close())
  
      override def getMaxFieldSize(): F[Int] = Sync[F].blocking(statement.getMaxFieldSize)
  
      override def setMaxFieldSize(max: Int): F[Unit] = Sync[F].blocking(statement.setMaxFieldSize(max))
  
      override def getMaxRows(): F[Int] = Sync[F].blocking(statement.getMaxRows)
  
      override def setMaxRows(max: Int): F[Unit] = Sync[F].blocking(statement.setMaxRows(max))
  
      override def setEscapeProcessing(enable: Boolean): F[Unit] = Sync[F].blocking(statement.setEscapeProcessing(enable))
  
      override def getQueryTimeout(): F[Int] = Sync[F].blocking(statement.getQueryTimeout)
  
      override def setQueryTimeout(seconds: Int): F[Unit] = Sync[F].blocking(statement.setQueryTimeout(seconds))
  
      override def cancel(): F[Unit] = Sync[F].blocking(statement.cancel())
  
      override def getWarnings(): F[SQLWarning] = Sync[F].blocking(statement.getWarnings)
  
      override def clearWarnings(): F[Unit] = Sync[F].blocking(statement.clearWarnings())
  
      override def setCursorName(name: String): F[Unit] = Sync[F].blocking(statement.setCursorName(name))
  
      override def execute(sql: String): F[Boolean] = Sync[F].blocking(statement.execute(sql))
  
      override def getResultSet(): F[ResultSet[F]] = Sync[F].blocking(statement.getResultSet).map(ResultSetIO[F])
  
      override def getUpdateCount(): F[Int] = Sync[F].blocking(statement.getUpdateCount)
  
      override def getMoreResults(): F[Boolean] = Sync[F].blocking(statement.getMoreResults)
  
      override def setFetchDirection(direction: Int): F[Unit] = Sync[F].blocking(statement.setFetchDirection(direction))
  
      override def getFetchDirection(): F[Int] = Sync[F].blocking(statement.getFetchDirection)
  
      override def setFetchSize(rows: Int): F[Unit] = Sync[F].blocking(statement.setFetchSize(rows))
  
      override def getFetchSize(): F[Int] = Sync[F].blocking(statement.getFetchSize)
  
      override def getResultSetConcurrency(): F[Int] = Sync[F].blocking(statement.getResultSetConcurrency)
  
      override def getResultSetType(): F[Int] = Sync[F].blocking(statement.getResultSetType)
  
      override def addBatch(sql: String): F[Unit] = Sync[F].blocking(statement.addBatch(sql))
  
      override def clearBatch(): F[Unit] = Sync[F].blocking(statement.clearBatch())
  
      override def executeBatch(): F[Array[Int]] = Sync[F].blocking(statement.executeBatch())
  
      override def getMoreResults(current: Int): F[Boolean] = Sync[F].blocking(statement.getMoreResults(current))
  
      override def getGeneratedKeys(): F[ResultSet[F]] = Sync[F].blocking(statement.getGeneratedKeys).map(ResultSetIO[F])
  
      override def executeUpdate(sql: String, autoGeneratedKeys: Statement.Generated): F[Int] =
        Sync[F].blocking(statement.executeUpdate(sql, autoGeneratedKeys.code))
  
      override def executeUpdate(sql: String, columnIndexes: Array[Int]): F[Int] =
        Sync[F].blocking(statement.executeUpdate(sql, columnIndexes))
  
      override def executeUpdate(sql: String, columnNames: Array[String]): F[Int] =
        Sync[F].blocking(statement.executeUpdate(sql, columnNames))
  
      override def execute(sql: String, autoGeneratedKeys: Statement.Generated): F[Boolean] =
        Sync[F].blocking(statement.execute(sql, autoGeneratedKeys.code))
  
      override def execute(sql: String, columnIndexes: Array[Int]): F[Boolean] =
        Sync[F].blocking(statement.execute(sql, columnIndexes))
  
      override def execute(sql: String, columnNames: Array[String]): F[Boolean] =
        Sync[F].blocking(statement.execute(sql, columnNames))
  
      override def getResultSetHoldability(): F[Int] = Sync[F].blocking(statement.getResultSetHoldability)
  
      override def isClosed(): F[Boolean] = Sync[F].blocking(statement.isClosed)
  
      override def setPoolable(poolable: Boolean): F[Unit] = Sync[F].blocking(statement.setPoolable(poolable))
  
      override def isPoolable(): F[Boolean] = Sync[F].blocking(statement.isPoolable)
  
      override def closeOnCompletion(): F[Unit] = Sync[F].blocking(statement.closeOnCompletion())
  
      override def isCloseOnCompletion(): F[Boolean] = Sync[F].blocking(statement.isCloseOnCompletion)
  
      override def getLargeUpdateCount(): F[Long] = Sync[F].blocking(statement.getLargeUpdateCount)
  
      override def setLargeMaxRows(max: Long): F[Unit] = Sync[F].blocking(statement.setLargeMaxRows(max))
  
      override def getLargeMaxRows(): F[Long] = Sync[F].blocking(statement.getLargeMaxRows)
  
      override def executeLargeBatch(): F[Array[Long]] = Sync[F].blocking(statement.executeLargeBatch())
  
      override def executeLargeUpdate(sql: String): F[Long] = Sync[F].blocking(statement.executeLargeUpdate(sql))
  
      override def executeLargeUpdate(sql: String, autoGeneratedKeys: Statement.Generated): F[Long] =
        Sync[F].blocking(statement.executeLargeUpdate(sql, autoGeneratedKeys.code))
  
      override def executeLargeUpdate(sql: String, columnIndexes: Array[Int]): F[Long] =
        Sync[F].blocking(statement.executeLargeUpdate(sql, columnIndexes))
  
      override def executeLargeUpdate(sql: String, columnNames: Array[String]): F[Long] =
        Sync[F].blocking(statement.executeLargeUpdate(sql, columnNames))
  