/** Copyright (c) 2023-2024 by Takahiko Tominaga This software is licensed under the MIT License (MIT). For more
  * information see LICENSE or https://opensource.org/licenses/MIT
  */

package ldbc.dsl

trait internalSyntax
  extends internal.ConnectionSyntax,
          internal.ResultSetMetaDataSyntax,
          internal.ParameterMetaDataSyntax
package object internal extends internalSyntax
