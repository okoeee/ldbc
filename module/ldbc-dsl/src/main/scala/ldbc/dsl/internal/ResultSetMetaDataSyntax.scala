/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.dsl.internal

import cats.effect.Sync

import ldbc.core.JdbcType
import ldbc.sql.ResultSetMetaData
import ResultSetMetaData.*

trait ResultSetMetaDataSyntax:

  implicit class ResultSetMetaDataF(resultSetMetaDataObject: ResultSetMetaData.type):

    def apply[F[_]: Sync](resultSetMetaData: java.sql.ResultSetMetaData): ResultSetMetaData[F] =
      new ResultSetMetaData[F]:

        override def getColumnCount(): F[Int] = Sync[F].blocking(resultSetMetaData.getColumnCount)

        override def isAutoIncrement(column: Int): F[Boolean] =
          Sync[F].blocking(resultSetMetaData.isAutoIncrement(column))

        override def isCaseSensitive(column: Int): F[Boolean] =
          Sync[F].blocking(resultSetMetaData.isCaseSensitive(column))

        override def isSearchable(column: Int): F[Boolean] = Sync[F].blocking(resultSetMetaData.isSearchable(column))

        override def isCurrency(column: Int): F[Boolean] = Sync[F].blocking(resultSetMetaData.isCurrency(column))

        override def isNullable(column: Int): F[Option[ColumnNull]] =
          Sync[F].blocking(ColumnNull.values.find(_.code == resultSetMetaData.isNullable(column)))

        override def isSigned(column: Int): F[Boolean] = Sync[F].blocking(resultSetMetaData.isSigned(column))

        override def getColumnDisplaySize(column: Int): F[Int] =
          Sync[F].blocking(resultSetMetaData.getColumnDisplaySize(column))

        override def getColumnLabel(column: Int): F[String] = Sync[F].blocking(resultSetMetaData.getColumnLabel(column))

        override def getColumnName(column: Int): F[String] = Sync[F].blocking(resultSetMetaData.getColumnName(column))

        override def getSchemaName(column: Int): F[String] = Sync[F].blocking(resultSetMetaData.getSchemaName(column))

        override def getPrecision(column: Int): F[Int] = Sync[F].blocking(resultSetMetaData.getPrecision(column))

        override def getScale(column: Int): F[Int] = Sync[F].blocking(resultSetMetaData.getScale(column))

        override def getTableName(column: Int): F[String] = Sync[F].blocking(resultSetMetaData.getTableName(column))

        override def getCatalogName(column: Int): F[String] = Sync[F].blocking(resultSetMetaData.getCatalogName(column))

        override def getColumnType(column: Int): F[JdbcType] =
          Sync[F].blocking(JdbcType.fromCode(resultSetMetaData.getColumnType(column)))

        override def getColumnTypeName(column: Int): F[String] =
          Sync[F].blocking(resultSetMetaData.getColumnTypeName(column))

        override def isReadOnly(column: Int): F[Boolean] = Sync[F].blocking(resultSetMetaData.isReadOnly(column))

        override def isWritable(column: Int): F[Boolean] = Sync[F].blocking(resultSetMetaData.isWritable(column))

        override def isDefinitelyWritable(column: Int): F[Boolean] =
          Sync[F].blocking(resultSetMetaData.isDefinitelyWritable(column))

        override def getColumnClassName(column: Int): F[String] =
          Sync[F].blocking(resultSetMetaData.getColumnClassName(column))
