/**
 * Copyright (c) 2023-2024 by Takahiko Tominaga
 * This software is licensed under the MIT License (MIT).
 * For more information see LICENSE or https://opensource.org/licenses/MIT
 */

package ldbc.connector.authenticator

class CachingSha2PasswordPlugin extends Sha256PasswordPlugin:

  override def name: String = "caching_sha2_password"

object CachingSha2PasswordPlugin:
  def apply(): CachingSha2PasswordPlugin = new CachingSha2PasswordPlugin()
