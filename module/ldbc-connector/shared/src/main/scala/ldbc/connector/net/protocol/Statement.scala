/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector.net.protocol

import scala.collection.immutable.ListMap

import cats.*
import cats.syntax.all.*

import cats.effect.*

import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.trace.{ Tracer, Span }

import ldbc.connector.ResultSet
import ldbc.connector.data.EnumMySQLSetOption
import ldbc.connector.exception.SQLException
import ldbc.connector.net.Protocol
import ldbc.connector.net.packet.response.*
import ldbc.connector.net.packet.request.*

/**
 * <P>The object used for executing a static SQL statement
 * and returning the results it produces.
 * <P>
 * By default, only one <code>ResultSet</code> object per <code>Statement</code>
 * object can be open at the same time. Therefore, if the reading of one
 * <code>ResultSet</code> object is interleaved
 * with the reading of another, each must have been generated by
 * different <code>Statement</code> objects. All execution methods in the
 * <code>Statement</code> interface implicitly close a current
 * <code>ResultSet</code> object of the statement if an open one exists.
 * 
 * @tparam F
 *   The effect type
 */
trait Statement[F[_]]:

  /**
   * Holds batched commands
   */
  def batchedArgs: Ref[F, Vector[String]]

  /**
   * Executes the given SQL statement, which returns a single
   * <code>ResultSet</code> object.
   *<p>
   * <strong>Note:</strong>This method cannot be called on a
   * <code>PreparedStatement</code> or <code>CallableStatement</code>.
   * @param sql an SQL statement to be sent to the database, typically a
   *        static SQL <code>SELECT</code> statement
   * @return a <code>ResultSet</code> object that contains the data produced
   *         by the given query; never <code>null</code>
   */
  def executeQuery(sql: String): F[ResultSet[F]]

  /**
   * Executes the given SQL statement, which may be an <code>INSERT</code>,
   * <code>UPDATE</code>, or <code>DELETE</code> statement or an
   * SQL statement that returns nothing, such as an SQL DDL statement.
   *<p>
   * <strong>Note:</strong>This method cannot be called on a
   * <code>PreparedStatement</code> or <code>CallableStatement</code>.
   * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
   * <code>DELETE</code>; or an SQL statement that returns nothing,
   * such as a DDL statement.
   *
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
   *         or (2) 0 for SQL statements that return nothing
   */
  def executeUpdate(sql: String): F[Int]

  /**
   * Releases this <code>Statement</code> object's database
   * and JDBC resources immediately instead of waiting for
   * this to happen when it is automatically closed.
   * It is generally good practice to release resources as soon as
   * you are finished with them to avoid tying up database
   * resources.
   * <P>
   * Calling the method <code>close</code> on a <code>Statement</code>
   * object that is already closed has no effect.
   * <P>
   * <B>Note:</B>When a <code>Statement</code> object is
   * closed, its current <code>ResultSet</code> object, if one exists, is
   * also closed.
   */
  def close(): F[Unit]

  /**
   * Executes the given SQL statement, which may return multiple results.
   * In some (uncommon) situations, a single SQL statement may return
   * multiple result sets and/or update counts.  Normally you can ignore
   * this unless you are (1) executing a stored procedure that you know may
   * return multiple results or (2) you are dynamically executing an
   * unknown SQL string.
   * <P>
   * The <code>execute</code> method executes an SQL statement and indicates the
   * form of the first result.  You must then use the methods
   * <code>getResultSet</code> or <code>getUpdateCount</code>
   * to retrieve the result, and <code>getMoreResults</code> to
   * move to any subsequent result(s).
   * <p>
   *<strong>Note:</strong>This method cannot be called on a
   * <code>PreparedStatement</code> or <code>CallableStatement</code>.
   * @param sql any SQL statement
   * @return <code>true</code> if the first result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there are
   *         no results
   */
  def execute(sql: String): F[Boolean]

  /**
   *  Retrieves the current result as a <code>ResultSet</code> object.
   *  This method should be called only once per result.
   *
   * @return the current result as a <code>ResultSet</code> object or
   * <code>None</code> if the result is an update count or there are no more results
   */
  def getResultSet(): F[Option[ResultSet[F]]]

  /**
   * Retrieves any auto-generated keys created as a result of executing this Statement object.
   */
  def returningAutoGeneratedKey(sql: String): F[Int]

  /**
   * Adds the given SQL command to the current list of commands for this Statement object.
   * The commands in this list can be executed as a batch by calling the method executeBatch.
   * 
   * @param sql
   *   typically this is a SQL INSERT or UPDATE statement
   */
  def addBatch(sql: String): F[Unit] = batchedArgs.update(_ :+ sql)

  /**
   * Empties this Statement object's current list of SQL commands.
   */
  def clearBatch(): F[Unit] = batchedArgs.set(Vector.empty)

  /**
   * Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts.
   * The int elements of the array that is returned are ordered to correspond to the commands in the batch, which are ordered according to the order in which they were added to the batch.
   * The elements in the array returned by the method executeBatch may be one of the following:
   *
   * <OL>
   * <LI>A number greater than or equal to zero -- indicates that the
   * command was processed successfully and is an update count giving the
   * number of rows in the database that were affected by the command's
   * execution
   * <LI>A value of <code>SUCCESS_NO_INFO</code> -- indicates that the command was
   * processed successfully but that the number of rows affected is
   * unknown
   * <P>
   * If one of the commands in a batch update fails to execute properly,
   * this method throws a <code>BatchUpdateException</code>, and a JDBC
   * driver may or may not continue to process the remaining commands in
   * the batch.  However, the driver's behavior must be consistent with a
   * particular DBMS, either always continuing to process commands or never
   * continuing to process commands.  If the driver continues processing
   * after a failure, the array returned by the method
   * <code>BatchUpdateException.getUpdateCounts</code>
   * will contain as many elements as there are commands in the batch, and
   * at least one of the elements will be the following:
   *
   * <LI>A value of <code>EXECUTE_FAILED</code> -- indicates that the command failed
   * to execute successfully and occurs only if a driver continues to
   * process commands after a command fails
   * </OL>
   *
   * @return
   *   an array of update counts containing one element for each command in the batch. The elements of the array are ordered according to the order in which commands were added to the batch.
   */
  def executeBatch(): F[List[Int]]

object Statement:

  /**
   * The constant indicating that a batch statement executed successfully but that no count of the number of rows it affected is available.
   */
  val SUCCESS_NO_INFO = -2

  /**
   * The constant indicating that an error occurred while executing a batch statement.
   */
  val EXECUTE_FAILED = -3

  private[ldbc] case class Impl[F[_]: Temporal: Exchange: Tracer](
    protocol:             Protocol[F],
    serverVariables:      Map[String, String],
    batchedArgs:          Ref[F, Vector[String]],
    statementClosed:      Ref[F, Boolean],
    connectionClosed:     Ref[F, Boolean],
    currentResultSet:     Ref[F, Option[ResultSet[F]]],
    resultSetType:        Int = ResultSet.TYPE_FORWARD_ONLY,
    resultSetConcurrency: Int = ResultSet.CONCUR_READ_ONLY
  )(using ev: MonadError[F, Throwable])
    extends Statement[F]:

    private val attributes = protocol.initialPacket.attributes ++ List(Attribute("type", "Statement"))

    override def executeQuery(sql: String): F[ResultSet[F]] =
      checkClosed {
        checkNullOrEmptyQuery(sql) *> exchange[F, ResultSet[F]]("statement") { (span: Span[F]) =>
          span.addAttributes((attributes ++ List(Attribute("execute", "query"), Attribute("sql", sql)))*) *>
            protocol.resetSequenceId *>
            protocol.send(ComQueryPacket(sql, protocol.initialPacket.capabilityFlags, ListMap.empty)) *>
            protocol.receive(ColumnsNumberPacket.decoder(protocol.initialPacket.capabilityFlags)).flatMap {
              case _: OKPacket =>
                for
                  isResultSetClosed      <- Ref[F].of(false)
                  resultSetCurrentCursor <- Ref[F].of(0)
                  resultSetCurrentRow    <- Ref[F].of[Option[ResultSetRowPacket]](None)
                yield ResultSet
                  .empty(
                    serverVariables,
                    protocol.initialPacket.serverVersion,
                    isResultSetClosed,
                    resultSetCurrentCursor,
                    resultSetCurrentRow
                  )
              case error: ERRPacket => ev.raiseError(error.toException("Failed to execute query", sql))
              case result: ColumnsNumberPacket =>
                for
                  columnDefinitions <-
                    protocol.repeatProcess(
                      result.size,
                      ColumnDefinitionPacket.decoder(protocol.initialPacket.capabilityFlags)
                    )
                  resultSetRow <-
                    protocol.readUntilEOF[ResultSetRowPacket](
                      ResultSetRowPacket.decoder(protocol.initialPacket.capabilityFlags, columnDefinitions),
                      Vector.empty
                    )
                  isResultSetClosed      <- Ref[F].of(false)
                  resultSetCurrentCursor <- Ref[F].of(0)
                  resultSetCurrentRow    <- Ref[F].of(resultSetRow.headOption)
                yield ResultSet(
                  columnDefinitions,
                  resultSetRow,
                  serverVariables,
                  protocol.initialPacket.serverVersion,
                  isResultSetClosed,
                  resultSetCurrentCursor,
                  resultSetCurrentRow,
                  resultSetType,
                  resultSetConcurrency
                )
            }
        }
      }

    override def executeUpdate(sql: String): F[Int] =
      checkClosed {
        checkNullOrEmptyQuery(sql) *> exchange[F, Int]("statement") { (span: Span[F]) =>
          span.addAttributes(
            (attributes ++ List(Attribute("execute", "update"), Attribute("sql", sql)))*
          ) *> protocol.resetSequenceId *> (
            protocol.send(ComQueryPacket(sql, protocol.initialPacket.capabilityFlags, ListMap.empty)) *>
              protocol.receive(GenericResponsePackets.decoder(protocol.initialPacket.capabilityFlags)).flatMap {
                case result: OKPacket => ev.pure(result.affectedRows)
                case error: ERRPacket => ev.raiseError(error.toException("Failed to execute query", sql))
                case _: EOFPacket     => ev.raiseError(new SQLException("Unexpected EOF packet"))
              }
          )
        }
      }

    override def close(): F[Unit] = statementClosed.set(true)

    override def execute(sql: String): F[Boolean] =
      checkClosed {
        checkNullOrEmptyQuery(sql) *> executeQuery(sql).flatMap { resultSet =>
          currentResultSet.set(Some(resultSet)) *> resultSet.hasRows()
        }
      }

    override def getResultSet(): F[Option[ResultSet[F]]] = currentResultSet.get

    override def returningAutoGeneratedKey(sql: String): F[Int] =
      checkClosed {
        checkNullOrEmptyQuery(sql) *> exchange[F, Int]("statement") { (span: Span[F]) =>
          span.addAttributes(
            (attributes ++ List(Attribute("execute", "returning update"), Attribute("sql", sql)))*
          ) *> protocol.resetSequenceId *> (
            protocol.send(ComQueryPacket(sql, protocol.initialPacket.capabilityFlags, ListMap.empty)) *>
              protocol.receive(GenericResponsePackets.decoder(protocol.initialPacket.capabilityFlags)).flatMap {
                case result: OKPacket => ev.pure(result.lastInsertId)
                case error: ERRPacket => ev.raiseError(error.toException("Failed to execute query", sql))
                case _: EOFPacket     => ev.raiseError(new SQLException("Unexpected EOF packet"))
              }
          )
        }
      }

    override def executeBatch(): F[List[Int]] =
      checkClosed {
        protocol.resetSequenceId *>
          protocol.comSetOption(EnumMySQLSetOption.MYSQL_OPTION_MULTI_STATEMENTS_ON) *>
          exchange[F, List[Int]]("statement") { (span: Span[F]) =>
            batchedArgs.get.flatMap { args =>
              span.addAttributes(
                (attributes ++ List(
                  Attribute("execute", "batch"),
                  Attribute("size", args.length.toLong),
                  Attribute("sql", args.toArray.toSeq)
                ))*
              ) *> (
                if args.isEmpty then ev.pure(List.empty)
                else
                  protocol.resetSequenceId *>
                    protocol.send(
                      ComQueryPacket(args.mkString(";"), protocol.initialPacket.capabilityFlags, ListMap.empty)
                    ) *>
                    args
                      .foldLeft(ev.pure(Vector.empty[Int])) { ($acc, _) =>
                        for
                          acc <- $acc
                          result <-
                            protocol
                              .receive(GenericResponsePackets.decoder(protocol.initialPacket.capabilityFlags))
                              .flatMap {
                                case result: OKPacket => ev.pure(acc :+ result.affectedRows)
                                case error: ERRPacket =>
                                  ev.raiseError(error.toException("Failed to execute batch", acc))
                                case _: EOFPacket => ev.raiseError(new SQLException("Unexpected EOF packet"))
                              }
                        yield result
                      }
                      .map(_.toList)
              )
            }
          } <* protocol.resetSequenceId <* protocol.comSetOption(
            EnumMySQLSetOption.MYSQL_OPTION_MULTI_STATEMENTS_OFF
          ) <* clearBatch()
      }

    private def checkClosed[T](f: => F[T]): F[T] =
      for
        statementClosed  <- statementClosed.get
        connectionClosed <- connectionClosed.get
        result <- (if statementClosed || connectionClosed then
                     ev.raiseError(new SQLException("No operations allowed after statement closed."))
                   else f)
      yield result

    private def checkNullOrEmptyQuery(sql: String): F[Unit] =
      if sql.isEmpty then ev.raiseError(new SQLException("Can not issue empty query."))
      else if sql == null then ev.raiseError(new SQLException("Can not issue NULL query."))
      else ev.unit

  def apply[F[_]: Temporal: Exchange: Tracer](
    protocol:             Protocol[F],
    serverVariables:      Map[String, String],
    batchedArgsRef:       Ref[F, Vector[String]],
    statementClosed:      Ref[F, Boolean],
    connectionClosed:     Ref[F, Boolean],
    currentResultSet:     Ref[F, Option[ResultSet[F]]],
    resultSetType:        Int = ResultSet.TYPE_FORWARD_ONLY,
    resultSetConcurrency: Int = ResultSet.CONCUR_READ_ONLY
  )(using ev: MonadError[F, Throwable]): Statement[F] =
    Impl(
      protocol,
      serverVariables,
      batchedArgsRef,
      statementClosed,
      connectionClosed,
      currentResultSet,
      resultSetType,
      resultSetConcurrency
    )
