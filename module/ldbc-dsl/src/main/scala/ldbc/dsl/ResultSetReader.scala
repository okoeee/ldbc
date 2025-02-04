/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.dsl

import java.time.{ ZoneId, Instant, ZonedDateTime, LocalTime, LocalDate, LocalDateTime }

import scala.compiletime.*

import cats.{ Functor, Monad }
import cats.implicits.*

import ldbc.sql.ResultSet

/**
 * Trait to get the DataType that matches the Scala type information from the ResultSet.
 *
 * @tparam F
 *   The effect type
 * @tparam T
 *   Scala types that match SQL DataType
 */
trait ResultSetReader[F[_], T]:

  /**
   * Method to retrieve data from a ResultSet using column names.
   *
   * @param resultSet
   *   A table of data representing a database result set, which is usually generated by executing a statement that
   *   queries the database.
   * @param columnLabel
   *   Column name of the data to be retrieved from the ResultSet.
   */
  def read(resultSet: ResultSet[F], columnLabel: String): F[T]

  /**
   * Method to retrieve data from a ResultSet using an Index number.
   *
   * @param resultSet
   *   A table of data representing a database result set, which is usually generated by executing a statement that
   *   queries the database.
   * @param index
   *   Index number of the data to be retrieved from the ResultSet.
   */
  def read(resultSet: ResultSet[F], index: Int): F[T]

object ResultSetReader:

  def apply[F[_], T](
    readLabel: ResultSet[F] => String => F[T],
    readIndex: ResultSet[F] => Int => F[T]
  ): ResultSetReader[F, T] =
    new ResultSetReader[F, T]:
      override def read(resultSet: ResultSet[F], columnLabel: String): F[T] =
        readLabel(resultSet)(columnLabel)

      override def read(resultSet: ResultSet[F], index: Int): F[T] =
        readIndex(resultSet)(index)

  /**
   * A method to convert the specified Scala type to an arbitrary type so that it can be handled by ResultSetReader.
   *
   * @param f
   *   Function to convert from type A to B.
   * @param reader
   *   ResultSetReader to retrieve the DataType matching the type A information from the ResultSet.
   * @tparam F
   *   The effect type
   * @tparam A
   *   The Scala type to be converted from.
   * @tparam B
   *   The Scala type to be converted to.
   */
  def mapping[F[_]: Functor, A, B](f: A => B)(using reader: ResultSetReader[F, A]): ResultSetReader[F, B] =
    reader.map(f(_))

  given [F[_]: Functor]: Functor[[T] =>> ResultSetReader[F, T]] with
    override def map[A, B](fa: ResultSetReader[F, A])(f: A => B): ResultSetReader[F, B] =
      ResultSetReader(
        resultSet => columnLabel => fa.read(resultSet, columnLabel).map(f),
        resultSet => index => fa.read(resultSet, index).map(f)
      )

  given [F[_]]: ResultSetReader[F, String]        = ResultSetReader(_.getString, _.getString)
  given [F[_]]: ResultSetReader[F, Boolean]       = ResultSetReader(_.getBoolean, _.getBoolean)
  given [F[_]]: ResultSetReader[F, Byte]          = ResultSetReader(_.getByte, _.getByte)
  given [F[_]]: ResultSetReader[F, Array[Byte]]   = ResultSetReader(_.getBytes, _.getBytes)
  given [F[_]]: ResultSetReader[F, Short]         = ResultSetReader(_.getShort, _.getShort)
  given [F[_]]: ResultSetReader[F, Int]           = ResultSetReader(_.getInt, _.getInt)
  given [F[_]]: ResultSetReader[F, Long]          = ResultSetReader(_.getLong, _.getLong)
  given [F[_]]: ResultSetReader[F, Float]         = ResultSetReader(_.getFloat, _.getFloat)
  given [F[_]]: ResultSetReader[F, Double]        = ResultSetReader(_.getDouble, _.getDouble)
  given [F[_]]: ResultSetReader[F, LocalDate]     = ResultSetReader(_.getDate, _.getDate)
  given [F[_]]: ResultSetReader[F, LocalTime]     = ResultSetReader(_.getTime, _.getTime)
  given [F[_]]: ResultSetReader[F, LocalDateTime] = ResultSetReader(_.getTimestamp, _.getTimestamp)
  given [F[_]]: ResultSetReader[F, BigDecimal]    = ResultSetReader(_.getBigDecimal, _.getBigDecimal)

  given [F[_]: Functor](using reader: ResultSetReader[F, String]): ResultSetReader[F, BigInt] =
    reader.map(str => if str == null then null else BigInt(str))

  given [F[_]: Functor](using reader: ResultSetReader[F, Instant]): ResultSetReader[F, ZonedDateTime] =
    reader.map(instant => if instant == null then null else ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))

  given [F[_]: Monad, A](using reader: ResultSetReader[F, A]): ResultSetReader[F, Option[A]] with

    override def read(resultSet: ResultSet[F], columnLabel: String): F[Option[A]] =
      for
        result <- reader.read(resultSet, columnLabel)
        bool   <- resultSet.wasNull()
      yield if bool then None else Some(result)

    override def read(resultSet: ResultSet[F], index: Int): F[Option[A]] =
      for
        result <- reader.read(resultSet, index)
        bool   <- resultSet.wasNull()
      yield if bool then None else Some(result)

  type MapToTuple[F[_], T <: Tuple] <: Tuple = T match
    case EmptyTuple => EmptyTuple
    case h *: t     => ResultSetReader[F, h] *: MapToTuple[F, t]

  inline def infer[F[_], T]: ResultSetReader[F, T] =
    summonFrom[ResultSetReader[F, T]] {
      case reader: ResultSetReader[F, T] => reader
      case _                             => error("ResultSetReader cannot be inferred")
    }

  inline def fold[F[_], T <: Tuple]: MapToTuple[F, T] =
    inline erasedValue[T] match
      case _: EmptyTuple => EmptyTuple
      case _: (h *: t)   => infer[F, h] *: fold[F, t]
