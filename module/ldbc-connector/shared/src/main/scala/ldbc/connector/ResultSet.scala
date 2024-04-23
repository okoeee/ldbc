/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector

import java.time.*

import ldbc.connector.util.Version
import ldbc.connector.exception.SQLException
import ldbc.connector.net.packet.response.*
import ldbc.connector.codec.all.*
import ldbc.connector.codec.Codec

/**
 * A table of data representing a database result set, which is usually generated by executing a statement that queries the database.
 */
trait ResultSet:

  /**
   * Moves the cursor forward one row from its current position.
   * A <code>ResultSet</code> cursor is initially positioned
   * before the first row; the first call to the method
   * <code>next</code> makes the first row the current row; the
   * second call makes the second row the current row, and so on.
   * <p>
   * When a call to the <code>next</code> method returns <code>false</code>,
   * the cursor is positioned after the last row. Any
   * invocation of a <code>ResultSet</code> method which requires a
   * current row will result in a <code>SQLException</code> being thrown.
   *  If the result set type is <code>TYPE_FORWARD_ONLY</code>, it is vendor specified
   * whether their JDBC driver implementation will return <code>false</code> or
   *  throw an <code>SQLException</code> on a
   * subsequent call to <code>next</code>.
   *
   * <P>If an input stream is open for the current row, a call
   * to the method <code>next</code> will
   * implicitly close it. A <code>ResultSet</code> object's
   * warning chain is cleared when a new row is read.
   *
   * @return <code>true</code> if the new current row is valid;
   * <code>false</code> if there are no more rows
   */
  def next(): Boolean

  /**
   * Releases this <code>ResultSet</code> object's database and
   * LDBC resources immediately instead of waiting for
   * this to happen when it is automatically closed.
   *
   * <P>The closing of a <code>ResultSet</code> object does <strong>not</strong> close the <code>Blob</code>,
   * <code>Clob</code> or <code>NClob</code> objects created by the <code>ResultSet</code>. <code>Blob</code>,
   * <code>Clob</code> or <code>NClob</code> objects remain valid for at least the duration of the
   * transaction in which they are created, unless their <code>free</code> method is invoked.
   *<p>
   * When a <code>ResultSet</code> is closed, any <code>ResultSetMetaData</code>
   * instances that were created by calling the  <code>getMetaData</code>
   * method remain accessible.
   *
   * <P><B>Note:</B> A <code>ResultSet</code> object
   * is automatically closed by the
   * <code>Statement</code> object that generated it when
   * that <code>Statement</code> object is closed,
   * re-executed, or is used to retrieve the next result from a
   * sequence of multiple results.
   *<p>
   * Calling the method <code>close</code> on a <code>ResultSet</code>
   * object that is already closed is a no-op.
   */
  def close(): Unit

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>String</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getString(columnIndex: Int): Option[String]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Boolean</code> in the Scala programming language.
   *
   * <P>If the designated column has a datatype of CHAR or VARCHAR
   * and contains a "0" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
   * and contains  a 0, a value of <code>false</code> is returned.  If the designated column has a datatype
   * of CHAR or VARCHAR
   * and contains a "1" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
   * and contains  a 1, a value of <code>true</code> is returned.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>false</code>
   */
  def getBoolean(columnIndex: Int): Boolean

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Byte</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getByte(columnIndex: Int): Byte

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Short</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getShort(columnIndex: Int): Short

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>Int</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getInt(columnIndex: Int): Int

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Long</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getLong(columnIndex: Int): Long

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Float</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getFloat(columnIndex: Int): Float

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Double</code> in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getDouble(columnIndex: Int): Double

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Byte</code> array in the Scala programming language.
   * The bytes represent the raw values returned by the driver.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getBytes(columnIndex: Int): Option[Array[Byte]]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalDate</code> object in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getDate(columnIndex: Int): Option[LocalDate]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalTime</code> object in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getTime(columnIndex: Int): Option[LocalTime]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalDateTime</code> object in the Scala programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getTimestamp(columnIndex: Int): Option[LocalDateTime]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>String</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getString(columnLabel: String): Option[String]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Boolean</code> in the Scala programming language.
   *
   * <P>If the designated column has a datatype of CHAR or VARCHAR
   * and contains a "0" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
   * and contains  a 0, a value of <code>false</code> is returned.  If the designated column has a datatype
   * of CHAR or VARCHAR
   * and contains a "1" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
   * and contains  a 1, a value of <code>true</code> is returned.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>false</code>
   */
  def getBoolean(columnLabel: String): Boolean

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Byte</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getByte(columnLabel: String): Byte

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Short</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getShort(columnLabel: String): Short

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>Int</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getInt(columnLabel: String): Int

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Long</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getLong(columnLabel: String): Long

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Float</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getFloat(columnLabel: String): Float

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>Double</code> in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   */
  def getDouble(columnLabel: String): Double

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>byte</code> array in the Scala programming language.
   * The bytes represent the raw values returned by the driver.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getBytes(columnLabel: String): Option[Array[Byte]]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalDate</code> object in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getDate(columnLabel: String): Option[LocalDate]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalTime</code> object in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>None</code>
   */
  def getTime(columnLabel: String): Option[LocalTime]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.time.LocalDateTime</code> object in the Scala programming language.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>None</code>
   */
  def getTimestamp(columnLabel: String): Option[LocalDateTime]

  /**
   * Retrieves the  number, types and properties of
   * this <code>ResultSet</code> object's columns.
   *
   * @return the description of this <code>ResultSet</code> object's columns
   */
  def getMetaData(): ResultSetMetaData

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>scala.math.BigDecimal</code> with full precision.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value (full precision);
   * if the value is SQL <code>NULL</code>, the value returned is
   * <code>None</code> in the Scala programming language.
   */
  def getBigDecimal(columnIndex: Int): Option[BigDecimal]

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>scala.math.BigDecimal</code> with full precision.
   *
   * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
   * @return the column value (full precision);
   * if the value is SQL <code>NULL</code>, the value returned is
   * <code>None</code> in the Scala programming language.
   */
  def getBigDecimal(columnLabel: String): Option[BigDecimal]

  /**
   * Retrieves whether the cursor is before the first row in
   * this <code>ResultSet</code> object.
   * <p>
   * <strong>Note:</strong>Support for the <code>isBeforeFirst</code> method
   * is optional for <code>ResultSet</code>s with a result
   * set type of <code>TYPE_FORWARD_ONLY</code>
   *
   * @return <code>true</code> if the cursor is before the first row;
   * <code>false</code> if the cursor is at any other position or the
   * result set contains no rows
   */
  def isBeforeFirst(): Boolean

  /**
   * Retrieves whether the cursor is on the first row of
   * this <code>ResultSet</code> object.
   * <p>
   * <strong>Note:</strong>Support for the <code>isFirst</code> method
   * is optional for <code>ResultSet</code>s with a result
   * set type of <code>TYPE_FORWARD_ONLY</code>
   *
   * @return <code>true</code> if the cursor is on the first row;
   * <code>false</code> otherwise
   */
  def isFirst(): Boolean

  /**
   * Retrieves whether the cursor is after the last row in
   * this <code>ResultSet</code> object.
   * <p>
   * <strong>Note:</strong>Support for the <code>isAfterLast</code> method
   * is optional for <code>ResultSet</code>s with a result
   * set type of <code>TYPE_FORWARD_ONLY</code>
   *
   * @return <code>true</code> if the cursor is after the last row;
   * <code>false</code> if the cursor is at any other position or the
   * result set contains no rows
   */
  def isAfterLast(): Boolean

  /**
   * Retrieves whether the cursor is on the last row of
   * this <code>ResultSet</code> object.
   *  <strong>Note:</strong> Calling the method <code>isLast</code> may be expensive
   * because the LDBC
   * might need to fetch ahead one row in order to determine
   * whether the current row is the last row in the result set.
   * <p>
   * <strong>Note:</strong> Support for the <code>isLast</code> method
   * is optional for <code>ResultSet</code>s with a result
   * set type of <code>TYPE_FORWARD_ONLY</code>
   * @return <code>true</code> if the cursor is on the last row;
   * <code>false</code> otherwise
   */
  def isLast(): Boolean

  /**
   * Moves the cursor to the front of
   * this <code>ResultSet</code> object, just before the
   * first row. This method has no effect if the result set contains no rows.
   */
  def beforeFirst(): Unit

  /**
   * Moves the cursor to the end of
   * this <code>ResultSet</code> object, just after the
   * last row. This method has no effect if the result set contains no rows.
   */
  def afterLast(): Unit

  /**
   * Moves the cursor to the first row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   */
  def first(): Boolean

  /**
   * Moves the cursor to the last row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   */
  def last(): Boolean

  /**
   * Retrieves the current row number.  The first row is number 1, the
   * second number 2, and so on.
   * <p>
   * <strong>Note:</strong>Support for the <code>getRow</code> method
   * is optional for <code>ResultSet</code>s with a result
   * set type of <code>TYPE_FORWARD_ONLY</code>
   *
   * @return the current row number; <code>0</code> if there is no current row
   */
  def getRow(): Int

  /**
   * Moves the cursor to the given row number in
   * this <code>ResultSet</code> object.
   *
   * <p>If the row number is positive, the cursor moves to
   * the given row number with respect to the
   * beginning of the result set.  The first row is row 1, the second
   * is row 2, and so on.
   *
   * <p>If the given row number is negative, the cursor moves to
   * an absolute row position with respect to
   * the end of the result set.  For example, calling the method
   * <code>absolute(-1)</code> positions the
   * cursor on the last row; calling the method <code>absolute(-2)</code>
   * moves the cursor to the next-to-last row, and so on.
   *
   * <p>If the row number specified is zero, the cursor is moved to
   * before the first row.
   *
   * <p>An attempt to position the cursor beyond the first/last row in
   * the result set leaves the cursor before the first row or after
   * the last row.
   *
   * <p><B>Note:</B> Calling <code>absolute(1)</code> is the same
   * as calling <code>first()</code>. Calling <code>absolute(-1)</code>
   * is the same as calling <code>last()</code>.
   *
   * @param row the number of the row to which the cursor should move.
   *        A value of zero indicates that the cursor will be positioned
   *        before the first row; a positive number indicates the row number
   *        counting from the beginning of the result set; a negative number
   *        indicates the row number counting from the end of the result set
   * @return <code>true</code> if the cursor is moved to a position in this
   */
  def absolute(row: Int): Boolean

  /**
   * Moves the cursor a relative number of rows, either positive or negative.
   * Attempting to move beyond the first/last row in the
   * result set positions the cursor before/after the
   * the first/last row. Calling <code>relative(0)</code> is valid, but does
   * not change the cursor position.
   *
   * <p>Note: Calling the method <code>relative(1)</code>
   * is identical to calling the method <code>next()</code> and
   * calling the method <code>relative(-1)</code> is identical
   * to calling the method <code>previous()</code>.
   *
   * @param rows an <code>int</code> specifying the number of rows to
   *        move from the current row; a positive number moves the cursor
   *        forward; a negative number moves the cursor backward
   * @return <code>true</code> if the cursor is on a row;
   *         <code>false</code> otherwise
   */
  def relative(rows: Int): Boolean

  /**
   * Moves the cursor to the previous row in this
   * <code>ResultSet</code> object.
   *<p>
   * When a call to the <code>previous</code> method returns <code>false</code>,
   * the cursor is positioned before the first row.  Any invocation of a
   * <code>ResultSet</code> method which requires a current row will result in a
   * <code>SQLException</code> being thrown.
   *<p>
   *
   * @return <code>true</code> if the cursor is now positioned on a valid row;
   * <code>false</code> if the cursor is positioned before the first row
   */
  def previous(): Boolean

  /**
   * Retrieves the type of this <code>ResultSet</code> object.
   * The type is determined by the <code>Statement</code> object
   * that created the result set.
   *
   * @return <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>,
   *         or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   */
  def getType(): Int

  /**
   * Function to decode all lines with the specified type.
   *
   * @param codec
   *   The codec to decode the value
   * @tparam T
   *   The type of the value
   * @return
   *   A list of values decoded with the specified type.
   */
  def decode[T](codec: Codec[T]): List[T]

object ResultSet:

  /**
   * The constant indicating the type for a <code>ResultSet</code> object
   * whose cursor may move only forward.
   */
  val TYPE_FORWARD_ONLY: Int = 1003

  /**
   * The constant indicating the type for a <code>ResultSet</code> object
   * that is scrollable but generally not sensitive to changes to the data
   * that underlies the <code>ResultSet</code>.
   */
  val TYPE_SCROLL_INSENSITIVE: Int = 1004

  /**
   * The constant indicating the type for a <code>ResultSet</code> object
   * that is scrollable and generally sensitive to changes to the data
   * that underlies the <code>ResultSet</code>.
   */
  val TYPE_SCROLL_SENSITIVE: Int = 1005

  private[ldbc] case class Impl(
    columns:       Vector[ColumnDefinitionPacket],
    records:       Vector[ResultSetRowPacket],
    version:       Version,
    resultSetType: Int = TYPE_FORWARD_ONLY
  ) extends ResultSet:

    private var isClosed:      Boolean                    = false
    private var currentCursor: Int                        = 0
    private var currentRow:    Option[ResultSetRowPacket] = records.headOption

    def next(): Boolean =
      checkClose {
        if currentCursor <= records.size then
          currentRow = records.lift(currentCursor)
          currentCursor += 1
          currentRow.isDefined
        else
          currentCursor += 1
          false
      }

    override def close(): Unit = isClosed = true

    override def getString(columnIndex: Int): Option[String] =
      checkClose {
        currentRow.flatMap(row => text.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getBoolean(columnIndex: Int): Boolean =
      checkClose {
        currentRow
          .flatMap(row => boolean.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
          .getOrElse(false)
      }

    override def getByte(columnIndex: Int): Byte =
      checkClose {
        currentRow.flatMap(row => tinyint.decode(columnIndex, List(row.values(columnIndex - 1))).toOption).getOrElse(0)
      }

    override def getShort(columnIndex: Int): Short =
      checkClose {
        currentRow.flatMap(row => smallint.decode(columnIndex, List(row.values(columnIndex - 1))).toOption).getOrElse(0)
      }

    override def getInt(columnIndex: Int): Int =
      checkClose {
        currentRow.flatMap(row => int.decode(columnIndex, List(row.values(columnIndex - 1))).toOption).getOrElse(0)
      }

    override def getLong(columnIndex: Int): Long =
      checkClose {
        currentRow.flatMap(row => bigint.decode(columnIndex, List(row.values(columnIndex - 1))).toOption).getOrElse(0L)
      }

    override def getFloat(columnIndex: Int): Float =
      checkClose {
        currentRow.flatMap(row => float.decode(columnIndex, List(row.values(columnIndex - 1))).toOption).getOrElse(0f)
      }

    override def getDouble(columnIndex: Int): Double =
      checkClose {
        currentRow
          .flatMap(row => double.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
          .getOrElse(0.toDouble)
      }

    override def getBytes(columnIndex: Int): Option[Array[Byte]] =
      checkClose {
        currentRow.flatMap(row => binary(255).decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getDate(columnIndex: Int): Option[LocalDate] =
      checkClose {
        currentRow.flatMap(row => date.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getTime(columnIndex: Int): Option[LocalTime] =
      checkClose {
        currentRow.flatMap(row => time.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getTimestamp(columnIndex: Int): Option[LocalDateTime] =
      checkClose {
        currentRow.flatMap(row => timestamp.decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getString(columnLabel: String): Option[String] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getString(index + 1)
        }
      }

    override def getBoolean(columnLabel: String): Boolean =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).exists {
          case (_, index) =>
            getBoolean(index + 1)
        }
      }

    override def getByte(columnLabel: String): Byte =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getByte(index + 1)
          }
          .getOrElse(0)
      }

    override def getShort(columnLabel: String): Short =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getShort(index + 1)
          }
          .getOrElse(0)
      }

    override def getInt(columnLabel: String): Int =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getInt(index + 1)
          }
          .getOrElse(0)
      }

    override def getLong(columnLabel: String): Long =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getLong(index + 1)
          }
          .getOrElse(0L)
      }

    override def getFloat(columnLabel: String): Float =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getFloat(index + 1)
          }
          .getOrElse(0f)
      }

    override def getDouble(columnLabel: String): Double =
      checkClose {
        columns.zipWithIndex
          .find(_._1.name == columnLabel)
          .map {
            case (_, index) =>
              getDouble(index + 1)
          }
          .getOrElse(0.toDouble)
      }

    override def getBytes(columnLabel: String): Option[Array[Byte]] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getBytes(index + 1)
        }
      }

    override def getDate(columnLabel: String): Option[LocalDate] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getDate(index + 1)
        }
      }

    override def getTime(columnLabel: String): Option[LocalTime] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getTime(index + 1)
        }
      }

    override def getTimestamp(columnLabel: String): Option[LocalDateTime] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getTimestamp(index + 1)
        }
      }

    override def getMetaData(): ResultSetMetaData =
      checkClose {
        ResultSetMetaData(columns, version)
      }

    override def getBigDecimal(columnIndex: Int): Option[BigDecimal] =
      checkClose {
        currentRow.flatMap(row => decimal().decode(columnIndex, List(row.values(columnIndex - 1))).toOption)
      }

    override def getBigDecimal(columnLabel: String): Option[BigDecimal] =
      checkClose {
        columns.zipWithIndex.find(_._1.name == columnLabel).flatMap {
          case (_, index) =>
            getBigDecimal(index + 1)
        }
      }

    override def isBeforeFirst(): Boolean = currentCursor <= 0 && records.nonEmpty

    override def isAfterLast(): Boolean = currentCursor > records.size && records.nonEmpty

    override def isFirst(): Boolean = currentCursor > 0

    override def isLast(): Boolean = currentCursor == records.size

    override def beforeFirst(): Unit =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else currentCursor = 0

    override def afterLast(): Unit =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else currentCursor = records.size + 1

    override def first(): Boolean =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else
        currentCursor = 1
        currentRow    = records.headOption
        currentRow.isDefined && records.nonEmpty

    override def last(): Boolean =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else
        currentCursor = records.size
        currentRow    = records.lastOption
        currentRow.isDefined && records.nonEmpty

    override def getRow(): Int =
      if currentCursor > records.size then 0
      else currentCursor

    override def absolute(row: Int): Boolean =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else if row > 0 then
        currentCursor = row
        currentRow    = records.lift(row - 1)
        row >= 1 && row <= records.size
      else if row < 0 then
        val position = records.size + row + 1
        currentCursor = position
        currentRow    = records.lift(records.size + row)
        position >= 1 && position <= records.size
      else
        currentCursor = 0
        currentRow    = None
        false

    override def relative(rows: Int): Boolean =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else
        val position = currentCursor + rows
        if position >= 1 && position <= records.size then
          currentCursor = position
          currentRow    = records.lift(position - 1)
          true
        else
          currentCursor = 0
          currentRow    = records.lift(currentCursor)
          false

    override def previous(): Boolean =
      if resultSetType == TYPE_FORWARD_ONLY then
        throw new SQLException("Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY.")
      else if currentCursor > 0 then
        currentCursor -= 1
        currentRow = records.lift(currentCursor - 1)
        currentRow.isDefined
      else
        currentCursor = 0
        currentRow    = None
        false

    override def getType(): Int = resultSetType

    override def decode[T](codec: Codec[T]): List[T] =
      checkClose {
        records.flatMap(row => codec.decode(0, row.values).toOption).toList
      }

    private def checkClose[T](f: => T): T =
      if isClosed then throw new SQLException("Operation not allowed after ResultSet closed")
      else f

  def apply(columns: Vector[ColumnDefinitionPacket], records: Vector[ResultSetRowPacket], version: Version): ResultSet =
    Impl(columns, records, version, ResultSet.TYPE_FORWARD_ONLY)

  def apply(
    columns:       Vector[ColumnDefinitionPacket],
    records:       Vector[ResultSetRowPacket],
    version:       Version,
    resultSetType: Int
  ): ResultSet =
    Impl(columns, records, version, resultSetType)

  def empty(version: Version): ResultSet = this.apply(Vector.empty, Vector.empty, version)
