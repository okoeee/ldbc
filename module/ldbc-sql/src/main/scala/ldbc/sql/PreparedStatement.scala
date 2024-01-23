/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.sql

/**
 * An object that represents a precompiled SQL statement.
 *
 * A SQL statement is precompiled and stored in a PreparedStatement object. This object can then be used to efficiently
 * execute this statement multiple times.
 *
 * Note: The setter methods (setShort, setString, and so on) for setting IN parameter values must specify types that are
 * compatible with the defined SQL type of the input parameter. For instance, if the IN parameter has SQL type INTEGER,
 * then the method setInt should be used.
 *
 * If arbitrary parameter type conversions are required, the method setObject should be used with a target SQL type.
 *
 * @tparam F
 *   The effect type
 */
trait PreparedStatement[F[_]]:

  /**
   * Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated by the query.
   *
   * @return
   *   a ResultSet object that contains the data produced by the query; never null
   */
  def executeQuery(): F[ResultSet[F]]

  /**
   * Executes the SQL statement in this PreparedStatement object, which must be an SQL Data Manipulation Language (DML)
   * statement, such as INSERT, UPDATE or DELETE; or an SQL statement that returns nothing, such as a DDL statement.
   *
   * @return
   *   either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that
   *   return nothing
   */
  def executeUpdate(): F[Int]

  /**
   * Releases this Statement object's database and JDBC resources immediately instead of waiting for this to happen when
   * it is automatically closed. It is generally good practice to release resources as soon as you are finished with
   * them to avoid tying up database resources.
   *
   * Calling the method close on a Statement object that is already closed has no effect.
   *
   * Note:When a Statement object is closed, its current ResultSet object, if one exists, is also closed.
   */
  def close(): F[Unit]

  /**
   * Sets the designated parameter to SQL NULL.
   *
   * Note: You must specify the parameter's SQL type.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param sqlType
   *   the SQL type code defined in [[java.sql.Types]]
   * @return
   */
  def setNull(parameterIndex: Int, sqlType: Int): F[Unit]

  /**
   * Sets the designated parameter to SQL NULL. This version of the method setNull should be used for user-defined types
   * and REF type parameters. Examples of user-defined types include: STRUCT, DISTINCT, JAVA_OBJECT, and named array
   * types.
   *
   * Note: To be portable, applications must give the SQL type code and the fully-qualified SQL type name when
   * specifying a NULL user-defined or REF parameter. In the case of a user-defined type the name is the type name of
   * the parameter itself. For a REF parameter, the name is the type name of the referenced type. If a JDBC driver does
   * not need the type code or type name information, it may ignore it. Although it is intended for user-defined and Ref
   * parameters, this method may be used to set a null parameter of any JDBC type. If the parameter does not have a
   * user-defined or REF type, the given typeName is ignored.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param sqlType
   *   the SQL type code defined in [[java.sql.Types]]
   * @param typeName
   *   the fully-qualified name of an SQL user-defined type; ignored if the parameter is not a user-defined type or REF
   */
  def setNull(parameterIndex: Int, sqlType: Int, typeName: String): F[Unit]

  /**
   * Sets the designated parameter to the given Scala boolean value. The driver converts this to an SQL BIT or BOOLEAN
   * value when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setBoolean(parameterIndex: Int, x: Boolean): F[Unit]

  /**
   * Sets the designated parameter to the given Scala byte value. The driver converts this to an SQL TINYINT value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setByte(parameterIndex: Int, x: Byte): F[Unit]

  /**
   * Sets the designated parameter to the given Scala short value. The driver converts this to an SQL SMALLINT value
   * when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setShort(parameterIndex: Int, x: Short): F[Unit]

  /**
   * Sets the designated parameter to the given Scala int value. The driver converts this to an SQL INTEGER value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setInt(parameterIndex: Int, x: Int): F[Unit]

  /**
   * Sets the designated parameter to the given Scala long value. The driver converts this to an SQL BIGINT value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setLong(parameterIndex: Int, x: Long): F[Unit]

  /**
   * Sets the designated parameter to the given Scala float value. The driver converts this to an SQL REAL value when it
   * sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setFloat(parameterIndex: Int, x: Float): F[Unit]

  /**
   * Sets the designated parameter to the given Scala double value. The driver converts this to an SQL DOUBLE value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setDouble(parameterIndex: Int, x: Double): F[Unit]

  /**
   * Sets the designated parameter to the given Scala.math.BigDecimal value. The driver converts this to an SQL NUMERIC
   * value when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setBigDecimal(parameterIndex: Int, x: BigDecimal): F[Unit]

  /**
   * Sets the designated parameter to the given Scala String value. The driver converts this to an SQL VARCHAR or
   * LONGVARCHAR value (depending on the argument's size relative to the driver's limits on VARCHAR values) when it
   * sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setString(parameterIndex: Int, x: String): F[Unit]

  /**
   * Sets the designated parameter to the given Scala array of bytes. The driver converts this to an SQL VARBINARY or
   * LONGVARBINARY (depending on the argument's size relative to the driver's limits on VARBINARY values) when it sends
   * it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setBytes(parameterIndex: Int, x: Array[Byte]): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Date value using the default time zone of the virtual machine
   * that is running the application. The driver converts this to an SQL DATE value when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setDate(parameterIndex: Int, x: java.sql.Date): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Date value, using the given Calendar object. The driver uses
   * the Calendar object to construct an SQL DATE value, which the driver then sends to the database. With a Calendar
   * object, the driver can calculate the date taking into account a custom timezone. If no Calendar object is
   * specified, the driver uses the default timezone, which is that of the virtual machine running the application.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   * @param cal
   *   the Calendar object the driver will use to construct the date
   */
  def setDate(parameterIndex: Int, x: java.sql.Date, cal: java.util.Calendar): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Time value. The driver converts this to an SQL TIME value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setTime(parameterIndex: Int, x: java.sql.Time): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Time value, using the given Calendar object. The driver uses
   * the Calendar object to construct an SQL TIME value, which the driver then sends to the database. With a Calendar
   * object, the driver can calculate the time taking into account a custom timezone. If no Calendar object is
   * specified, the driver uses the default timezone, which is that of the virtual machine running the application.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   * @param cal
   *   the Calendar object the driver will use to construct the time
   */
  def setTime(parameterIndex: Int, x: java.sql.Time, cal: java.util.Calendar): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Timestamp value. The driver converts this to an SQL TIMESTAMP
   * value when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setTimestamp(parameterIndex: Int, x: java.sql.Timestamp): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Timestamp value, using the given Calendar object. The driver
   * uses the Calendar object to construct an SQL TIMESTAMP value, which the driver then sends to the database. With a
   * Calendar object, the driver can calculate the timestamp taking into account a custom timezone. If no Calendar
   * object is specified, the driver uses the default timezone, which is that of the virtual machine running the
   * application.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   * @param cal
   *   the Calendar object the driver will use to construct the time
   */
  def setTimestamp(parameterIndex: Int, x: java.sql.Timestamp, cal: java.util.Calendar): F[Unit]

  /**
   * Sets the designated parameter to the given input stream, which will have the specified number of bytes. When a very
   * large ASCII value is input to a LONGVARCHAR parameter, it may be more practical to send it via a
   * java.io.InputStream. Data will be read from the stream as needed until end-of-file is reached. The JDBC driver will
   * do any necessary conversion from ASCII to the database char format.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the Java input stream that contains the ASCII parameter value
   * @param length
   *   the number of bytes in the stream
   */
  def setAsciiStream(parameterIndex: Int, x: java.io.InputStream, length: Int): F[Unit]

  /**
   * Sets the designated parameter to the given input stream. When a very large ASCII value is input to a LONGVARCHAR
   * parameter, it may be more practical to send it via a java.io.InputStream. Data will be read from the stream as
   * needed until end-of-file is reached. The JDBC driver will do any necessary conversion from ASCII to the database
   * char format.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of
   * setAsciiStream which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the Java input stream that contains the ASCII parameter value
   */
  def setAsciiStream(parameterIndex: Int, x: java.io.InputStream): F[Unit]

  /**
   * Sets the designated parameter to the given input stream, which will have the specified number of bytes. When a very
   * large binary value is input to a LONGVARBINARY parameter, it may be more practical to send it via a
   * java.io.InputStream object. The data will be read from the stream as needed until end-of-file is reached.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the java input stream which contains the binary parameter value
   * @param length
   *   the number of bytes in the stream
   */
  def setBinaryStream(parameterIndex: Int, x: java.io.InputStream, length: Int): F[Unit]

  /**
   * Sets the designated parameter to the given input stream. When a very large binary value is input to a LONGVARBINARY
   * parameter, it may be more practical to send it via a java.io.InputStream object. The data will be read from the
   * stream as needed until end-of-file is reached.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of
   * setBinaryStream which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the java input stream which contains the binary parameter value
   */
  def setBinaryStream(parameterIndex: Int, x: java.io.InputStream): F[Unit]

  /**
   * Clears the current parameter values immediately.
   *
   * In general, parameter values remain in force for repeated use of a statement. Setting a parameter value
   * automatically clears its previous value. However, in some cases it is useful to immediately release the resources
   * used by the current parameter values; this can be done by calling the method clearParameters.
   */
  def clearParameters(): F[Unit]

  /**
   * Sets the value of the designated parameter with the given object. This method is similar to
   * setObject(parameterIndex: Int, x: Object, targetSqlType: Int, scaleOrLength: Int), except that it assumes a scale
   * of zero.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the object containing the input parameter value
   * @param targetSqlType
   *   the SQL type (as defined in java.sql.Types) to be sent to the database
   */
  def setObject(parameterIndex: Int, x: Object, targetSqlType: Int): F[Unit]

  /**
   * Sets the value of the designated parameter using the given object.
   *
   * The JDBC specification specifies a standard mapping from Java Object types to SQL types. The given argument will be
   * converted to the corresponding SQL type before being sent to the database.
   *
   * Note that this method may be used to pass datatabase- specific abstract data types, by using a driver-specific Java
   * type. If the object is of a class implementing the interface SQLData, the JDBC driver should call the method
   * SQLData.writeSQL to write it to the SQL data stream. If, on the other hand, the object is of a class implementing
   * Ref, Blob, Clob, NClob, Struct, java.net.URL, RowId, SQLXML or Array, the driver should pass it to the database as
   * a value of the corresponding SQL type.
   *
   * Note: Not all databases allow for a non-typed Null to be sent to the backend. For maximum portability, the setNull
   * or the setObject(parameterIndex: Int, x: Object, sqlType: Int) method should be used instead of
   * setObject(parameterIndex: Int, x: Object).
   *
   * Note: This method throws an exception if there is an ambiguity, for example, if the object is of a class
   * implementing more than one of the interfaces named above.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the object containing the input parameter value
   */
  def setObject(parameterIndex: Int, x: Object): F[Unit]

  /**
   * Executes the SQL statement in this PreparedStatement object, which may be any kind of SQL statement. Some prepared
   * statements return multiple results; the execute method handles these complex statements as well as the simpler form
   * of statements handled by the methods executeQuery and executeUpdate.
   *
   * The execute method returns a boolean to indicate the form of the first result. You must call either the method
   * getResultSet or getUpdateCount to retrieve the result; you must call getMoreResults to move to any subsequent
   * result(s).
   *
   * @return
   *   true if the first result is a ResultSet object; false if the first result is an update count or there is no
   *   result
   */
  def execute(): F[Boolean]

  /**
   * Adds a set of parameters to this PreparedStatement object's batch of commands.
   */
  def addBatch(): F[Unit]

  /**
   * Sets the designated parameter to the given Reader object, which is the given number of characters long. When a very
   * large UNICODE value is input to a LONGVARCHAR parameter, it may be more practical to send it via a java.io.Reader
   * object. The data will be read from the stream as needed until end-of-file is reached. The JDBC driver will do any
   * necessary conversion from UNICODE to the database char format.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   the java.io.Reader object that contains the Unicode data
   * @param length
   *   the number of characters in the stream
   */
  def setCharacterStream(parameterIndex: Int, reader: java.io.Reader, length: Int): F[Unit]

  /**
   * Sets the designated parameter to the given Reader object. When a very large UNICODE value is input to a LONGVARCHAR
   * parameter, it may be more practical to send it via a java.io.Reader object. The data will be read from the stream
   * as needed until end-of-file is reached. The JDBC driver will do any necessary conversion from UNICODE to the
   * database char format.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of
   * setCharacterStream which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   the java.io.Reader object that contains the Unicode data
   */
  def setCharacterStream(parameterIndex: Int, reader: java.io.Reader): F[Unit]

  /**
   * Sets the designated parameter to the given REF(&lt;structured-type&gt;) value. The driver converts this to an SQL
   * REF value when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   an SQL REF value
   */
  def setRef(parameterIndex: Int, x: java.sql.Ref): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Blob object. The driver converts this to an SQL BLOB value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   a Blob object that maps an SQL BLOB value
   */
  def setBlob(parameterIndex: Int, x: java.sql.Blob): F[Unit]

  /**
   * Sets the designated parameter to a InputStream object. The inputstream must contain the number of characters
   * specified by length otherwise a SQLException will be generated when the PreparedStatement is executed. This method
   * differs from the setBinaryStream (int, InputStream, int) method because it informs the driver that the parameter
   * value should be sent to the server as a BLOB. When the setBinaryStream method is used, the driver may have to do
   * extra work to determine whether the parameter data should be sent to the server as a LONGVARBINARY or a BLOB
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param inputStream
   *   An object that contains the data to set the parameter value to.
   * @param length
   *   the number of bytes in the parameter data.
   */
  def setBlob(parameterIndex: Int, inputStream: java.io.InputStream, length: Int): F[Unit]

  /**
   * Sets the designated parameter to a InputStream object. This method differs from the setBinaryStream (int,
   * InputStream) method because it informs the driver that the parameter value should be sent to the server as a BLOB.
   * When the setBinaryStream method is used, the driver may have to do extra work to determine whether the parameter
   * data should be sent to the server as a LONGVARBINARY or a BLOB
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of setBlob
   * which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param inputStream
   *   An object that contains the data to set the parameter value to.
   */
  def setBlob(parameterIndex: Int, inputStream: java.io.InputStream): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Clob object. The driver converts this to an SQL CLOB value when
   * it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   a Clob object that maps an SQL CLOB value
   */
  def setClob(parameterIndex: Int, x: java.sql.Clob): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. The reader must contain the number of characters specified by
   * length otherwise a SQLException will be generated when the PreparedStatement is executed. This method differs from
   * the setCharacterStream (int, Reader, int) method because it informs the driver that the parameter value should be
   * sent to the server as a CLOB. When the setCharacterStream method is used, the driver may have to do extra work to
   * determine whether the parameter data should be sent to the server as a LONGVARCHAR or a CLOB
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   An object that contains the data to set the parameter value to.
   * @param length
   *   the number of characters in the parameter data.
   */
  def setClob(parameterIndex: Int, reader: java.io.Reader, length: Int): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. This method differs from the setCharacterStream (int, Reader)
   * method because it informs the driver that the parameter value should be sent to the server as a CLOB. When the
   * setCharacterStream method is used, the driver may have to do extra work to determine whether the parameter data
   * should be sent to the server as a LONGVARCHAR or a CLOB
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of setClob
   * which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   An object that contains the data to set the parameter value to.
   */
  def setClob(parameterIndex: Int, reader: java.io.Reader): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.Array object. The driver converts this to an SQL ARRAY value
   * when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   an Array object that maps an SQL ARRAY value
   */
  def setArray(parameterIndex: Int, x: java.sql.Array): F[Unit]

  /**
   * Retrieves a ResultSetMetaData object that contains information about the columns of the ResultSet object that will
   * be returned when this PreparedStatement object is executed.
   *
   * Because a PreparedStatement object is precompiled, it is possible to know about the ResultSet object that it will
   * return without having to execute it. Consequently, it is possible to invoke the method getMetaData on a
   * PreparedStatement object rather than waiting to execute it and then invoking the ResultSet.getMetaData method on
   * the ResultSet object that is returned.
   *
   * NOTE: Using this method may be expensive for some drivers due to the lack of underlying DBMS support.
   *
   * @return
   *   the description of a ResultSet object's columns or null if the driver cannot return a ResultSetMetaData object
   */
  def getMetaData(): F[ResultSetMetaData[F]]

  /**
   * Sets the designated parameter to the given java.net.URL value. The driver converts this to an SQL DATALINK value
   * when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the java.net.URL object to be set
   */
  def setURL(parameterIndex: Int, x: java.net.URL): F[Unit]

  /**
   * Retrieves the number, types and properties of this PreparedStatement object's parameters.
   *
   * @return
   *   a ParameterMetaData object that contains information about the number, types and properties for each parameter
   *   marker of this PreparedStatement object
   */
  def getParameterMetaData(): F[ParameterMetaData[F]]

  /**
   * Sets the designated parameter to the given [[java.sql.RowId]] object. The driver converts this to a SQL ROWID value
   * when it sends it to the database
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setRowId(parameterIndex: Int, x: java.sql.RowId): F[Unit]

  /**
   * Sets the designated parameter to the given String object. The driver converts this to a SQL NCHAR or NVARCHAR or
   * LONGNVARCHAR value (depending on the argument's size relative to the driver's limits on NVARCHAR values) when it
   * sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the parameter value
   */
  def setNString(parameterIndex: Int, x: String): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. The Reader reads the data till end-of-file is reached. The driver
   * does the necessary conversion from Java character format to the national character set in the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param value
   *   the parameter value
   * @param length
   *   the number of characters in the parameter data.
   */
  def setNCharacterStream(parameterIndex: Int, value: java.io.Reader, length: Int): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. The Reader reads the data till end-of-file is reached. The driver
   * does the necessary conversion from Java character format to the national character set in the database.
   *
   * Note: This stream object can either be a standard Java stream object or your own subclass that implements the
   * standard interface.
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of
   * setNCharacterStream which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param value
   *   the parameter value
   */
  def setNCharacterStream(parameterIndex: Int, value: java.io.Reader): F[Unit]

  /**
   * Sets the designated parameter to a [[java.sql.NClob]] object. The driver converts this to a SQL NCLOB value when it
   * sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param value
   *   the parameter value
   */
  def setNClob(parameterIndex: Int, value: java.sql.NClob): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. The reader must contain the number of characters specified by
   * length otherwise a SQLException will be generated when the PreparedStatement is executed. This method differs from
   * the setCharacterStream (int, Reader, int) method because it informs the driver that the parameter value should be
   * sent to the server as a NCLOB. When the setCharacterStream method is used, the driver may have to do extra work to
   * determine whether the parameter data should be sent to the server as a LONGNVARCHAR or a NCLOB
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   An object that contains the data to set the parameter value to.
   * @param length
   *   the number of characters in the parameter data.
   */
  def setNClob(parameterIndex: Int, reader: java.io.Reader, length: Int): F[Unit]

  /**
   * Sets the designated parameter to a Reader object. This method differs from the setCharacterStream (int, Reader)
   * method because it informs the driver that the parameter value should be sent to the server as a NCLOB. When the
   * setCharacterStream method is used, the driver may have to do extra work to determine whether the parameter data
   * should be sent to the server as a LONGNVARCHAR or a NCLOB
   *
   * Note: Consult your JDBC driver documentation to determine if it might be more efficient to use a version of
   * setNClob which takes a length parameter.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param reader
   *   An object that contains the data to set the parameter value to.
   */
  def setNClob(parameterIndex: Int, reader: java.io.Reader): F[Unit]

  /**
   * Sets the designated parameter to the given java.sql.SQLXML object. The driver converts this to an SQL XML value
   * when it sends it to the database.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param xmlObject
   *   a SQLXML object that maps an SQL XML value
   */
  def setSQLXML(parameterIndex: Int, xmlObject: java.sql.SQLXML): F[Unit]

  /**
   * Sets the value of the designated parameter with the given object. If the second argument is an InputStream then the
   * stream must contain the number of bytes specified by scaleOrLength. If the second argument is a Reader then the
   * reader must contain the number of characters specified by scaleOrLength. If these conditions are not true the
   * driver will generate a SQLException when the prepared statement is executed.
   *
   * The given Java object will be converted to the given targetSqlType before being sent to the database. If the object
   * has a custom mapping (is of a class implementing the interface SQLData), the JDBC driver should call the method
   * SQLData.writeSQL to write it to the SQL data stream. If, on the other hand, the object is of a class implementing
   * Ref, Blob, Clob, NClob, Struct, java.net.URL, or Array, the driver should pass it to the database as a value of the
   * corresponding SQL type.
   *
   * Note that this method may be used to pass database-specific abstract data types.
   *
   * The default implementation will throw SQLFeatureNotSupportedException
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the object containing the input parameter value
   * @param targetSqlType
   *   the SQL type to be sent to the database. The scale argument may further qualify this type.
   * @param scaleOrLength
   *   for java.sql.JDBCType.DECIMAL or java.sql.JDBCType.NUMERIC types, this is the number of digits after the decimal
   *   point. For Java Object types InputStream and Reader, this is the length of the data in the stream or reader. For
   *   all other types, this value will be ignored.
   */
  def setObject(parameterIndex: Int, x: Object, targetSqlType: java.sql.SQLType, scaleOrLength: Int): F[Unit]

  /**
   * Sets the value of the designated parameter with the given object. If the second argument is an InputStream then the
   * stream must contain the number of bytes specified by scaleOrLength. If the second argument is a Reader then the
   * reader must contain the number of characters specified by scaleOrLength. If these conditions are not true the
   * driver will generate a SQLException when the prepared statement is executed.
   *
   * The given Java object will be converted to the given targetSqlType before being sent to the database. If the object
   * has a custom mapping (is of a class implementing the interface SQLData), the JDBC driver should call the method
   * SQLData.writeSQL to write it to the SQL data stream. If, on the other hand, the object is of a class implementing
   * Ref, Blob, Clob, NClob, Struct, java.net.URL, or Array, the driver should pass it to the database as a value of the
   * corresponding SQL type.
   *
   * Note that this method may be used to pass database-specific abstract data types.
   *
   * @param parameterIndex
   *   the first parameter is 1, the second is 2, ...
   * @param x
   *   the object containing the input parameter value
   * @param targetSqlType
   *   the SQL type (as defined in java.sql.Types) to be sent to the database. The scale argument may further qualify
   *   this type.
   * @param scaleOrLength
   *   for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types, this is the number of digits after the decimal point.
   *   For Java Object types InputStream and Reader, this is the length of the data in the stream or reader. For all
   *   other types, this value will be ignored.
   */
  def setObject(parameterIndex: Int, x: Object, targetSqlType: Int, scaleOrLength: Int): F[Unit]

  /**
   * Executes the SQL statement in this PreparedStatement object, which must be an SQL Data Manipulation Language (DML)
   * statement, such as INSERT, UPDATE or DELETE; or an SQL statement that returns nothing, such as a DDL statement.
   *
   * This method should be used when the returned row count may exceed Integer.MAX_VALUE.
   *
   * @return
   *   either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that
   *   return nothing
   */
  def executeLargeUpdate(): F[Long]

  /**
   * Retrieves any auto-generated keys created as a result of executing this Statement object. If this Statement object
   * did not generate any keys, an empty ResultSet object is returned.
   *
   * Note:If the columns which represent the auto-generated keys were not specified, the JDBC driver implementation will
   * determine the columns which best represent the auto-generated keys.
   *
   * @return
   *   a ResultSet object containing the auto-generated key(s) generated by the execution of this Statement object
   */
  def getGeneratedKeys(): F[ResultSet[F]]
