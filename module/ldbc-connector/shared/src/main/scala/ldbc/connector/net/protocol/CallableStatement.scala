/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector.net.protocol

import java.time.*

import scala.collection.immutable.ListMap

import cats.*
import cats.syntax.all.*

import cats.effect.*

import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.trace.{ Tracer, Span }

import ldbc.connector.*
import ldbc.connector.data.*
import ldbc.connector.sql.*
import ldbc.connector.exception.SQLException
import ldbc.connector.net.Protocol
import ldbc.connector.net.packet.response.*
import ldbc.connector.net.packet.request.*

/**
 * The interface used to execute SQL stored procedures.  The JDBC API
 * provides a stored procedure SQL escape syntax that allows stored procedures
 * to be called in a standard way for all RDBMSs. This escape syntax has one
 * form that includes a result parameter and one that does not. If used, the result
 * parameter must be registered as an OUT parameter. The other parameters
 * can be used for input, output or both. Parameters are referred to
 * sequentially, by number, with the first parameter being 1.
 * <PRE>
 *   {?= call &lt;procedure-name&gt;[(&lt;arg1&gt;,&lt;arg2&gt;, ...)]}
 *   {call &lt;procedure-name&gt;[(&lt;arg1&gt;,&lt;arg2&gt;, ...)]}
 * </PRE>
 * <P>
 * IN parameter values are set using the <code>set</code> methods inherited from
 * {@link PreparedStatement}.  The type of all OUT parameters must be
 * registered prior to executing the stored procedure; their values
 * are retrieved after execution via the <code>get</code> methods provided here.
 * <P>
 * A <code>CallableStatement</code> can return one {@link ResultSet} object or
 * multiple <code>ResultSet</code> objects.  Multiple
 * <code>ResultSet</code> objects are handled using operations
 * inherited from {@link Statement}.
 * <P>
 * For maximum portability, a call's <code>ResultSet</code> objects and
 * update counts should be processed prior to getting the values of output
 * parameters.
 *
 * @tparam F
 *   the effect type
 */
trait CallableStatement[F[_]] extends PreparedStatement[F]:

  private[ldbc] def setParameter(index: Int, value: String): F[Unit] =
    params.update(_ + (index -> Parameter.parameter(value)))

  /**
   * Registers the OUT parameter in ordinal position
   * <code>parameterIndex</code> to the JDBC type
   * <code>sqlType</code>.  All OUT parameters must be registered
   * before a stored procedure is executed.
   * <p>
   * The JDBC type specified by <code>sqlType</code> for an OUT
   * parameter determines the Java type that must be used
   * in the <code>get</code> method to read the value of that parameter.
   * <p>
   * If the JDBC type expected to be returned to this output parameter
   * is specific to this particular database, <code>sqlType</code>
   * should be <code>java.sql.Types.OTHER</code>.  The method
   * {@link #getObject} retrieves the value.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @param sqlType the JDBC type code defined by <code>java.sql.Types</code>.
   *        If the parameter is of JDBC type <code>NUMERIC</code>
   *        or <code>DECIMAL</code>, the version of
   *        <code>registerOutParameter</code> that accepts a scale value
   *        should be used.
   */
  def registerOutParameter(parameterIndex: Int, sqlType: Int): F[Unit]

  /**
   * Retrieves the value of the designated JDBC <code>CHAR</code>,
   * <code>VARCHAR</code>, or <code>LONGVARCHAR</code> parameter as a
   * <code>String</code> in the Java programming language.
   * <p>
   * For the fixed-length type JDBC <code>CHAR</code>,
   * the <code>String</code> object
   * returned has exactly the same value the SQL
   * <code>CHAR</code> value had in the
   * database, including any padding added by the database.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result
   *         is <code>None</code>.
   */
  def getString(parameterIndex: Int): F[Option[String]]

  /**
   * Retrieves the value of the designated JDBC <code>BIT</code>
   * or <code>BOOLEAN</code> parameter as a
   * <code>boolean</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result is <code>false</code>.
   */
  def getBoolean(parameterIndex: Int): F[Boolean]

  /**
   * Retrieves the value of the designated JDBC <code>TINYINT</code> parameter
   * as a <code>byte</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getByte(parameterIndex: Int): F[Byte]

  /**
   * Retrieves the value of the designated JDBC <code>SMALLINT</code> parameter
   * as a <code>short</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getShort(parameterIndex: Int): F[Short]

  /**
   * Retrieves the value of the designated JDBC <code>INTEGER</code> parameter
   * as an <code>int</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value.  If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getInt(parameterIndex: Int): F[Int]

  /**
   * Retrieves the value of the designated JDBC <code>BIGINT</code> parameter
   * as a <code>long</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getLong(parameterIndex: Int): F[Long]

  /**
   * Retrieves the value of the designated JDBC <code>FLOAT</code> parameter
   * as a <code>float</code> in the Java programming language.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>0</code>.
   */
  def getFloat(parameterIndex: Int): F[Float]

  /**
   * Retrieves the value of the designated JDBC <code>DOUBLE</code> parameter as a <code>double</code>
   * in the Java programming language.
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>0</code>.
   */
  def getDouble(parameterIndex: Int): F[Double]

  /**
   * Retrieves the value of the designated JDBC <code>BINARY</code> or
   * <code>VARBINARY</code> parameter as an array of <code>byte</code>
   * values in the Java programming language.
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>None</code>.
   */
  def getBytes(parameterIndex: Int): F[Option[Array[Byte]]]

  /**
   * Retrieves the value of the designated JDBC <code>DATE</code> parameter as a
   * <code>java.time.LocalDate</code> object.
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>None</code>.
   */
  def getDate(parameterIndex: Int): F[Option[LocalDate]]

  /**
   * Retrieves the value of the designated JDBC <code>TIME</code> parameter as a
   * <code>java.time.LocalTime</code> object.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>null</code>.
   */
  def getTime(parameterIndex: Int): F[Option[LocalTime]]

  /**
   * Retrieves the value of the designated JDBC <code>TIMESTAMP</code> parameter as a
   * <code>java.time.LocalDateTime</code> object.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,
   *        and so on
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   *         is <code>None</code>.
   */
  def getTimestamp(parameterIndex: Int): F[Option[LocalDateTime]]

  /**
   * Retrieves the value of the designated JDBC <code>NUMERIC</code> parameter as a
   * <code>java.math.BigDecimal</code> object with as many digits to the
   * right of the decimal point as the value contains.
   * @param parameterIndex the first parameter is 1, the second is 2,
   * and so on
   * @return the parameter value in full precision.  If the value is
   * SQL <code>NULL</code>, the result is <code>None</code>.
   */
  def getBigDecimal(parameterIndex: Int): F[Option[BigDecimal]]

  /**
   * Retrieves the value of a JDBC <code>CHAR</code>, <code>VARCHAR</code>,
   * or <code>LONGVARCHAR</code> parameter as a <code>String</code> in
   * the Java programming language.
   * <p>
   * For the fixed-length type JDBC <code>CHAR</code>,
   * the <code>String</code> object
   * returned has exactly the same value the SQL
   * <code>CHAR</code> value had in the
   * database, including any padding added by the database.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>None</code>.
   */
  def getString(parameterName: String): F[Option[String]]

  /**
   * Retrieves the value of a JDBC <code>BIT</code> or <code>BOOLEAN</code>
   * parameter as a
   * <code>boolean</code> in the Java programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>false</code>.
   */
  def getBoolean(parameterName: String): F[Boolean]

  /**
   * Retrieves the value of a JDBC <code>TINYINT</code> parameter as a <code>byte</code>
   * in the Java programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getByte(parameterName: String): F[Byte]

  /**
   * Retrieves the value of a JDBC <code>SMALLINT</code> parameter as a <code>short</code>
   * in the Java programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>0</code>.
   */
  def getShort(parameterName: String): F[Short]

  /**
   * Retrieves the value of a JDBC <code>INTEGER</code> parameter as an <code>int</code>
   * in the Java programming language.
   *
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result is <code>0</code>.
   */
  def getInt(parameterName: String): F[Int]

  /**
   * Retrieves the value of a JDBC <code>BIGINT</code> parameter as a <code>long</code>
   * in the Java programming language.
   *
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result is <code>0</code>.
   */
  def getLong(parameterName: String): F[Long]

  /**
   * Retrieves the value of a JDBC <code>FLOAT</code> parameter as a <code>float</code>
   * in the Java programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result is <code>0</code>.
   */
  def getFloat(parameterName: String): F[Float]

  /**
   * Retrieves the value of a JDBC <code>DOUBLE</code> parameter as a <code>double</code>
   * in the Java programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>,
   *         the result is <code>0</code>.
   */
  def getDouble(parameterName: String): F[Double]

  /**
   * Retrieves the value of a JDBC <code>BINARY</code> or <code>VARBINARY</code>
   * parameter as an array of <code>byte</code> values in the Java
   * programming language.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result is
   *  <code>None</code>.
   */
  def getBytes(parameterName: String): F[Option[Array[Byte]]]

  /**
   * Retrieves the value of a JDBC <code>DATE</code> parameter as a
   * <code>java.sql.Date</code> object.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>None</code>.
   */
  def getDate(parameterName: String): F[Option[LocalDate]]

  /**
   * Retrieves the value of a JDBC <code>TIME</code> parameter as a
   * <code>java.sql.Time</code> object.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>null</code>.
   */
  def getTime(parameterName: String): F[Option[LocalTime]]

  /**
   * Retrieves the value of a JDBC <code>TIMESTAMP</code> parameter as a
   * <code>java.sql.Timestamp</code> object.
   * @param parameterName the name of the parameter
   * @return the parameter value. If the value is SQL <code>NULL</code>, the result
   * is <code>None</code>.
   */
  def getTimestamp(parameterName: String): F[Option[LocalDateTime]]

  /**
   * Retrieves the value of a JDBC <code>NUMERIC</code> parameter as a
   * <code>java.math.BigDecimal</code> object with as many digits to the
   * right of the decimal point as the value contains.
   * @param parameterName the name of the parameter
   * @return the parameter value in full precision. If the value is
   * SQL <code>NULL</code>, the result is <code>None</code>.
   */
  def getBigDecimal(parameterName: String): F[Option[BigDecimal]]

object CallableStatement:

  val NOT_OUTPUT_PARAMETER_INDICATOR: Int = Int.MinValue

  private val PARAMETER_NAMESPACE_PREFIX = "@com_mysql_ldbc_outparam_"

  case class CallableStatementParameter(
    paramName:     Option[String],
    isIn:          Boolean,
    isOut:         Boolean,
    index:         Int,
    jdbcType:      Int,
    typeName:      Option[String],
    precision:     Int,
    scale:         Int,
    nullability:   Short,
    inOutModifier: Int
  )

  case class ParamInfo(
    nativeSql:      String,
    dbInUse:        Option[String],
    isFunctionCall: Boolean,
    numParameters:  Int,
    parameterList:  List[CallableStatementParameter],
    parameterMap:   ListMap[String, CallableStatementParameter]
  )

  object ParamInfo:

    def apply[F[_]: Temporal](
      nativeSql:      String,
      database:       Option[String],
      resultSet:      ResultSet[F],
      isFunctionCall: Boolean
    ): F[ParamInfo] =
      val parameterListF = Monad[F].whileM[List, CallableStatementParameter](resultSet.next()) {
        for
          index           <- resultSet.getRow()
          paramName       <- resultSet.getString(4)
          procedureColumn <- resultSet.getInt(5)
          jdbcType        <- resultSet.getInt(6)
          typeName        <- resultSet.getString(7)
          precision       <- resultSet.getInt(8)
          scale           <- resultSet.getInt(19)
          nullability     <- resultSet.getShort(12)
        yield
          val inOutModifier = procedureColumn match
            case DatabaseMetaData.procedureColumnIn    => ParameterMetaData.parameterModeIn
            case DatabaseMetaData.procedureColumnInOut => ParameterMetaData.parameterModeInOut
            case DatabaseMetaData.procedureColumnOut | DatabaseMetaData.procedureColumnReturn =>
              ParameterMetaData.parameterModeOut
            case _ => ParameterMetaData.parameterModeUnknown

          val (isOutParameter, isInParameter) =
            if index - 1 == 0 && isFunctionCall then (true, false)
            else if inOutModifier == DatabaseMetaData.procedureColumnInOut then (true, true)
            else if inOutModifier == DatabaseMetaData.procedureColumnIn then (false, true)
            else if inOutModifier == DatabaseMetaData.procedureColumnOut then (true, false)
            else (false, false)
          CallableStatementParameter(
            paramName,
            isInParameter,
            isOutParameter,
            index,
            jdbcType,
            typeName,
            precision,
            scale,
            nullability,
            inOutModifier
          )
      }

      for
        numParameters <- resultSet.rowLength()
        parameterList <- parameterListF
      yield ParamInfo(
        nativeSql      = nativeSql,
        dbInUse        = database,
        isFunctionCall = isFunctionCall,
        numParameters  = numParameters,
        parameterList  = parameterList,
        parameterMap   = ListMap(parameterList.map(p => p.paramName.getOrElse("") -> p)*)
      )

  private[ldbc] case class Impl[F[_]: Temporal: Exchange: Tracer](
    protocol:                Protocol[F],
    serverVariables:         Map[String, String],
    sql:                     String,
    paramInfo:               ParamInfo,
    params:                  Ref[F, ListMap[Int, Parameter]],
    batchedArgs:             Ref[F, Vector[String]],
    connectionClosed:        Ref[F, Boolean],
    statementClosed:         Ref[F, Boolean],
    resultSetClosed:         Ref[F, Boolean],
    currentResultSet:        Ref[F, Option[ResultSet[F]]],
    outputParameterResults:  Ref[F, Option[ResultSet[F]]],
    parameterIndexToRsIndex: Ref[F, Map[Int, Int]],
    updateCount:             Ref[F, Int],
    moreResults:             Ref[F, Boolean],
    autoGeneratedKeys:       Ref[F, Statement.NO_GENERATED_KEYS | Statement.RETURN_GENERATED_KEYS],
    lastInsertId:            Ref[F, Int],
    resultSetType:           Int = ResultSet.TYPE_FORWARD_ONLY,
    resultSetConcurrency:    Int = ResultSet.CONCUR_READ_ONLY
  )(using ev: MonadError[F, Throwable])
    extends CallableStatement[F],
            Statement.ShareStatement[F]:

    private def buildQuery(original: String, params: ListMap[Int, Parameter]): String =
      val query = original.toCharArray
      params
        .foldLeft(query) {
          case (query, (offset, param)) =>
            val index = query.indexOf('?', offset - 1)
            if index < 0 then query
            else
              val (head, tail)         = query.splitAt(index)
              val (tailHead, tailTail) = tail.splitAt(1)
              head ++ param.sql ++ tailTail
        }
        .mkString

    private val attributes = protocol.initialPacket.attributes ++ List(
      Attribute("type", "CallableStatement"),
      Attribute("sql", sql)
    )

    override def executeQuery(): F[ResultSet[F]] =
      checkClosed() *>
        checkNullOrEmptyQuery(sql) *>
        exchange[F, ResultSet[F]]("statement") { (span: Span[F]) =>
          setInOutParamsOnServer(paramInfo) *>
            setOutParams(paramInfo) *>
            params.get.flatMap { params =>
              span.addAttributes(
                (attributes ++ List(
                  Attribute("params", params.map((_, param) => param.toString).mkString(", ")),
                  Attribute("execute", "query")
                ))*
              ) *>
                protocol.resetSequenceId *>
                protocol.send(
                  ComQueryPacket(buildQuery(sql, params), protocol.initialPacket.capabilityFlags, ListMap.empty)
                ) *>
                receiveUntilOkPacket(Vector.empty).flatMap { resultSets =>
                  resultSets.headOption match
                    case None =>
                      for
                        resultSetCurrentCursor <- Ref[F].of(0)
                        resultSetCurrentRow    <- Ref[F].of[Option[ResultSetRowPacket]](None)
                        resultSet = ResultSet.empty(
                                      serverVariables,
                                      protocol.initialPacket.serverVersion,
                                      resultSetClosed,
                                      resultSetCurrentCursor,
                                      resultSetCurrentRow
                                    )
                        _ <- currentResultSet.set(Some(resultSet))
                      yield resultSet
                    case Some(resultSet) =>
                      currentResultSet.update(_ => Some(resultSet)) *> resultSet.pure[F]
                }
            } <*
            params.set(ListMap.empty) <*
            retrieveOutParams()
        }

    override def executeUpdate(): F[Int]       = ???
    override def execute():       F[Boolean]   = ???
    override def addBatch():      F[Unit]      = ???
    override def executeBatch():  F[List[Int]] = ???
    override def close():         F[Unit]      = ???

    override def registerOutParameter(parameterIndex: Int, sqlType: Int): F[Unit] = ???

    override def getString(parameterIndex: Int): F[Option[String]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getString(index))
      yield value

    override def getBoolean(parameterIndex: Int): F[Boolean] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getBoolean(index))
      yield value

    override def getByte(parameterIndex: Int): F[Byte] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getByte(index))
      yield value

    override def getShort(parameterIndex: Int): F[Short] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getShort(index))
      yield value

    override def getInt(parameterIndex: Int): F[Int] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getInt(index))
      yield value

    override def getLong(parameterIndex: Int): F[Long] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getLong(index))
      yield value

    override def getFloat(parameterIndex: Int): F[Float] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getFloat(index))
      yield value

    override def getDouble(parameterIndex: Int): F[Double] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getDouble(index))
      yield value

    override def getBytes(parameterIndex: Int): F[Option[Array[Byte]]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getBytes(index))
      yield value

    override def getDate(parameterIndex: Int): F[Option[LocalDate]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getDate(index))
      yield value

    override def getTime(parameterIndex: Int): F[Option[LocalTime]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getTime(index))
      yield value

    override def getTimestamp(parameterIndex: Int): F[Option[LocalDateTime]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getTimestamp(index))
      yield value

    override def getBigDecimal(parameterIndex: Int): F[Option[BigDecimal]] =
      for
        resultSet <- checkBounds(parameterIndex) *> getOutputParameters()
        paramMap  <- parameterIndexToRsIndex.get
        index = paramMap.getOrElse(parameterIndex, parameterIndex)
        value <-
          (if index == NOT_OUTPUT_PARAMETER_INDICATOR then
             ev.raiseError(new SQLException(s"Parameter $parameterIndex is not registered as an output parameter"))
           else resultSet.getBigDecimal(index))
      yield value

    override def getString(parameterName: String): F[Option[String]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getString(mangleParameterName(parameterName))
      yield value

    override def getBoolean(parameterName: String): F[Boolean] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getBoolean(mangleParameterName(parameterName))
      yield value

    override def getByte(parameterName: String): F[Byte] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getByte(mangleParameterName(parameterName))
      yield value

    override def getShort(parameterName: String): F[Short] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getShort(mangleParameterName(parameterName))
      yield value

    override def getInt(parameterName: String): F[Int] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getInt(mangleParameterName(parameterName))
      yield value

    override def getLong(parameterName: String): F[Long] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getLong(mangleParameterName(parameterName))
      yield value

    override def getFloat(parameterName: String): F[Float] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getFloat(mangleParameterName(parameterName))
      yield value

    override def getDouble(parameterName: String): F[Double] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getDouble(mangleParameterName(parameterName))
      yield value

    override def getBytes(parameterName: String): F[Option[Array[Byte]]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getBytes(mangleParameterName(parameterName))
      yield value

    override def getDate(parameterName: String): F[Option[LocalDate]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getDate(mangleParameterName(parameterName))
      yield value

    override def getTime(parameterName: String): F[Option[LocalTime]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getTime(mangleParameterName(parameterName))
      yield value

    override def getTimestamp(parameterName: String): F[Option[LocalDateTime]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getTimestamp(mangleParameterName(parameterName))
      yield value

    override def getBigDecimal(parameterName: String): F[Option[BigDecimal]] =
      for
        resultSet <- getOutputParameters()
        value <- resultSet.getBigDecimal(mangleParameterName(parameterName))
      yield value

    private def sendQuery(sql: String): F[GenericResponsePackets] =
      checkNullOrEmptyQuery(sql) *> protocol.resetSequenceId *> protocol.send(
        ComQueryPacket(sql, protocol.initialPacket.capabilityFlags, ListMap.empty)
      ) *> protocol.receive(GenericResponsePackets.decoder(protocol.initialPacket.capabilityFlags))

    private def receiveUntilOkPacket(resultSets: Vector[ResultSet[F]]): F[Vector[ResultSet[F]]] =
      protocol.receive(ColumnsNumberPacket.decoder(protocol.initialPacket.capabilityFlags)).flatMap {
        case _: OKPacket      => resultSets.pure[F]
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
            resultSetCurrentCursor <- Ref[F].of(0)
            resultSetCurrentRow    <- Ref[F].of(resultSetRow.headOption)
            resultSet = ResultSet(
                          columnDefinitions,
                          resultSetRow,
                          serverVariables,
                          protocol.initialPacket.serverVersion,
                          resultSetClosed,
                          resultSetCurrentCursor,
                          resultSetCurrentRow,
                          resultSetType,
                          resultSetConcurrency
                        )
            resultSets <- receiveUntilOkPacket(resultSets :+ resultSet)
          yield resultSets
      }

    private def receiveQueryResult(): F[ResultSet[F]] =
      protocol.receive(ColumnsNumberPacket.decoder(protocol.initialPacket.capabilityFlags)).flatMap {
        case _: OKPacket =>
          for
            resultSetCurrentCursor <- Ref[F].of(0)
            resultSetCurrentRow    <- Ref[F].of[Option[ResultSetRowPacket]](None)
          yield ResultSet
            .empty(
              serverVariables,
              protocol.initialPacket.serverVersion,
              resultSetClosed,
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
            resultSetCurrentCursor <- Ref[F].of(0)
            resultSetCurrentRow    <- Ref[F].of(resultSetRow.headOption)
            resultSet = ResultSet(
                          columnDefinitions,
                          resultSetRow,
                          serverVariables,
                          protocol.initialPacket.serverVersion,
                          resultSetClosed,
                          resultSetCurrentCursor,
                          resultSetCurrentRow,
                          resultSetType,
                          resultSetConcurrency
                        )
            _ <- currentResultSet.set(Some(resultSet))
          yield resultSet
      }

    private def mangleParameterName(origParameterName: String): String =
      val offset = if origParameterName.nonEmpty && origParameterName.charAt(0) == '@' then 1 else 0

      val paramNameBuf = new StringBuilder(PARAMETER_NAMESPACE_PREFIX.length + origParameterName.length)
      paramNameBuf.append(PARAMETER_NAMESPACE_PREFIX)
      paramNameBuf.append(origParameterName.substring(offset))

      paramNameBuf.toString

    private def setInOutParamsOnServer(paramInfo: ParamInfo): F[Unit] =
      if paramInfo.numParameters > 0 then
        paramInfo.parameterList.foldLeft(ev.unit) { (acc, param) =>
          if param.isOut && param.isIn then
            val paramName          = param.paramName.getOrElse("nullnp" + param.index)
            val inOutParameterName = mangleParameterName(paramName)

            val queryBuf = new StringBuilder(4 + inOutParameterName.length + 1)
            queryBuf.append("SET ")
            queryBuf.append(inOutParameterName)
            queryBuf.append("=")

            acc *> params.get.flatMap { params =>
              params.get(param.index) match
                case Some(parameter) =>
                  val sql = (queryBuf.toString.toCharArray ++ parameter.sql).mkString
                  sendQuery(sql).flatMap {
                    case _: OKPacket      => ev.unit
                    case error: ERRPacket => ev.raiseError(error.toException("Failed to execute query", sql))
                    case _: EOFPacket     => ev.raiseError(new SQLException("Unexpected EOF packet"))
                  }
                case None => ev.raiseError(new SQLException("Parameter not found"))
            }
          else acc
        }
      else ev.unit

    private def setOutParams(paramInfo: ParamInfo): F[Unit] =
      if paramInfo.numParameters > 0 then
        paramInfo.parameterList.foldLeft(ev.unit) { (acc, param) =>
          if !paramInfo.isFunctionCall && param.isOut then
            val paramName        = param.paramName.getOrElse("nullnp" + param.index)
            val outParameterName = mangleParameterName(paramName)

            acc *> params.get.flatMap { params =>
              for
                outParamIndex <- (
                                   if params.isEmpty then ev.pure(param.index)
                                   else
                                     params.keys
                                       .find(_ == param.index)
                                       .fold(
                                         ev.raiseError(
                                           new SQLException(
                                             s"Parameter ${ param.index } is not registered as an output parameter"
                                           )
                                         )
                                       )(_.pure[F])
                                 )
                _ <- setParameter(outParamIndex, outParameterName)
              yield ()
            }
          else acc
        }
      else ev.unit

    /**
     * Issues a second query to retrieve all output parameters.
     */
    private def retrieveOutParams(): F[Unit] =
      val parameters = paramInfo.parameterList.foldLeft(Vector.empty[(Int, String)]) { (acc, param) =>
        if param.isOut then
          val paramName        = param.paramName.getOrElse("nullnp" + param.index)
          val outParameterName = mangleParameterName(paramName)
          acc :+ (param.index, outParameterName)
        else acc
      }

      if paramInfo.numParameters > 0 && parameters.nonEmpty then
        val outParameterQuery = new StringBuilder("SELECT ")

        parameters.zipWithIndex.foreach {
          case ((paramIndex, paramName), index) =>
            if index != 0 then
              outParameterQuery.append(", ")
              if !paramName.startsWith("@") then outParameterQuery.append("@")
              outParameterQuery.append(paramName)
            else
              if !paramName.startsWith("@") then outParameterQuery.append("@")
              outParameterQuery.append(paramName)
        }

        val sql = outParameterQuery.toString
        checkClosed() *>
          checkNullOrEmptyQuery(sql) *>
          protocol.resetSequenceId *>
          protocol.send(ComQueryPacket(sql, protocol.initialPacket.capabilityFlags, ListMap.empty)) *>
          receiveQueryResult().flatMap { resultSet =>
            outputParameterResults.update(_ => Some(resultSet))
          } *>
          parameters.zipWithIndex.foldLeft(ev.unit) {
            case (acc, ((paramIndex, _), index)) =>
              acc *> parameterIndexToRsIndex.update(_ + (paramIndex -> (index + 1)))
          }
      else ev.unit

    /**
     * Returns the ResultSet that holds the output parameters, or throws an
     * appropriate exception if none exist, or they weren't returned.
     *
     * @return
     *   the ResultSet that holds the output parameters
     */
    private def getOutputParameters(): F[ResultSet[F]] =
      outputParameterResults.get.flatMap {
        case None =>
          if paramInfo.numParameters == 0 then ev.raiseError(new SQLException("No output parameters registered."))
          else ev.raiseError(new SQLException("No output parameters returned by procedure."))
        case Some(resultSet) => resultSet.pure[F]
      }

    private def checkBounds(paramIndex: Int): F[Unit] =
      if paramIndex < 1 || paramIndex > paramInfo.numParameters then
        ev.raiseError(
          new SQLException(s"Parameter index of ${ paramIndex } is out of range (1, ${ paramInfo.numParameters })")
        )
      else ev.unit
