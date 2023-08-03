/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.dsl.statement

import ldbc.core.{ Column, Table }
import ldbc.dsl.ParameterBinder

/** A model for constructing ORDER BY statements in MySQL.
  *
  * @param table
  *   Trait for generating SQL table information.
  * @param statement
  *   SQL statement string
  * @param columns
  *   Union-type column list
  * @param params
  *   A list of Traits that generate values from Parameter, allowing PreparedStatement to be set to a value by index
  *   only.
  * @tparam F
  *   The effect type
  * @tparam P
  *   Base trait for all products
  * @tparam T
  *   Union type of column
  */
private[ldbc] case class OrderBy[F[_], P <: Product, T](
  table:     Table[P],
  statement: String,
  columns:   T,
  params:    Seq[ParameterBinder[F]]
) extends Query[F, T]

object OrderBy:

  /**
   * Trait to indicate the order of the order.
   */
  trait Order:

    /** Sort Order Type */
    def name: String

    /** Trait for representing SQL Column */
    def column: Column[?]

    /** SQL query string */
    def statement: String = s"${ column.label } $name"

    override def toString: String = statement

  case class Asc(column: Column[?]) extends Order:
    override def name: String = "ASC"
  case class Desc(column: Column[?]) extends Order:
    override def name: String = "DESC"

/**
 * Transparent Trait to provide orderBy method.
 *
 * @tparam F
 *   The effect type
 * @tparam P
 *   Base trait for all products
 * @tparam T
 *   Union type of column
 */
transparent private[ldbc] trait OrderByProvider[F[_], P <: Product, T](table: Table[P]):
  self: Query[F, T] =>

  def orderBy[A <: OrderBy.Order | OrderBy.Order *: NonEmptyTuple | Column[?]](func: Table[P] => A): OrderBy[F, P, T] =
    val order = func(table) match
      case v: Tuple => v.toList.mkString(", ")
      case v: OrderBy.Order => v.statement
      case v: Column[?] => v.label
    OrderBy(
      table = table,
      statement = self.statement ++ s" ORDER BY $order",
      columns = self.columns,
      params = self.params
    )
