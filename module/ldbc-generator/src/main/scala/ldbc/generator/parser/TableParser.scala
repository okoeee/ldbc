/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator.parser

import ldbc.generator.model.*

/** Parser for parsing Table definitions.
  */
trait TableParser extends KeyParser:

  private def temporary: Parser[String] = caseSensitivity("temporary") ^^ (_.toUpperCase)
  private def table:     Parser[String] = caseSensitivity("table") ^^ (_.toUpperCase)

  /** The AUTOEXTEND_SIZE option is a feature added in MySQL database version 8.0.23 and later. This is an option to set
    * whether the database file size is automatically extended.
    *
    * The AUTOEXTEND_SIZE option is a feature to automatically increase the size of the database, allowing for
    * flexibility in accommodating data growth.
    *
    * SEE: https://dev.mysql.com/doc/refman/8.0/ja/innodb-tablespace-autoextend-size.html
    */
  private def autoextendSize: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("autoextend_size"),
        """(4|8|12|16|20|24|28|32|36|40|44|48|52|56|60|64)""".r <~ caseSensitivity("m")
      ) ^^ (v => Table.Options.AutoExtendSize(v.toInt)),
      """
        |======================================================
        |There is an error in the autoextend_size format.
        |Please correct the format according to the following.
        |
        |The value set for AUTOEXTEND_SIZE must be a multiple of 4.
        |The minimum is 4 and the maximum is 64.
        |
        |example: AUTOEXTEND_SIZE[=]'size'M
        |======================================================
        |""".stripMargin
    )

  /** The initial AUTO_INCREMENT value for the table.
    *
    * In MySQL 8.0, this works for MyISAM, MEMORY, InnoDB, and ARCHIVE tables. To set the initial auto-increment value
    * for engines that do not support the AUTO_INCREMENT table option, insert a "dummy" row with a value one less than
    * the desired value after creating the table, then delete the dummy row.
    */
  private def autoIncrement: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("auto_increment"), digit) ^^ Table.Options.AutoIncrement.apply,
      """
        |======================================================
        |There is an error in the auto_increment format.
        |Please correct the format according to the following.
        |
        |Only numbers can be set for size.
        |It cannot be set smaller than the maximum value currently in the column.
        |
        |example: AUTO_INCREMENT[=]'size'
        |======================================================
        |""".stripMargin
    )

  /** An approximation of the average row length of the table. You only need to set this for large tables with variable
    * size rows.
    *
    * The AVG_ROW_LENGTH option is a setting to specify the average length of a single row. This setting allows for
    * efficient use of database space.
    */
  private def avgRowLength: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("avg_row_length"), digit) ^^ Table.Options.AVGRowLength.apply,
      """
        |======================================================
        |There is an error in the avg_row_length format.
        |Please correct the format according to the following.
        |
        |Only numbers can be set for size.
        |
        |example: AVG_ROW_LENGTH[=]'size'
        |======================================================
        |""".stripMargin
    )

  /** Specifies the default character set for the table. CHARSET is a synonym for CHARACTER SET. If the character set
    * name is DEFAULT, the database character set is used.
    */
  private def characterSet: Parser[Table.Options] =
    customError(
      opt(caseSensitivity("default")) ~> character ^^ Table.Options.Character.apply,
      """
        |======================================================
        |There is an error in the character format.
        |Please correct the format according to the following.
        |
        |Only numbers can be set for size.
        |
        |example: [DEFAULT] {CHARACTER [SET] | CHARSET} [=] 'string'
        |======================================================
        |""".stripMargin
    )

  /** The CHECKSUM option is one of the features used in the MySQL database. It is used to check data integrity.
    *
    * The CHECKSUM option has two setting values, 0 and 1. If set to 0, no checksum calculation is performed. When set
    * to 0, no checksum calculation is performed, i.e., data integrity is not checked. On the other hand, when set to 1,
    * the checksum is calculated and data integrity is checked.
    */
  private def checksum: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("checksum"), """(0|1)""".r) ^^ {
        case "0" => Table.Options.CheckSum(0)
        case "1" => Table.Options.CheckSum(1)
      },
      """
        |======================================================
        |There is an error in the checksum format.
        |Please correct the format according to the following.
        |
        |example: CHECKSUM [=] {0 | 1}
        |======================================================
        |""".stripMargin
    )

  /** Specifies the default collation for the table.
    */
  private def collateSet: Parser[Table.Options] =
    customError(
      opt(caseSensitivity("default")) ~> collate ^^ Table.Options.Collate.apply,
      """
        |======================================================
        |There is an error in the collate format.
        |Please correct the format according to the following.
        |
        |example: [DEFAULT] COLLATE [=] 'string'
        |======================================================
        |""".stripMargin
    )

  /** It is a comment of the table and can be up to 2048 characters in length.
    */
  private def commentOption: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("comment"), stringLiteral) ^^ Table.Options.Comment.apply,
      """
        |======================================================
        |There is an error in the comment format.
        |Please correct the format according to the following.
        |
        |example: COMMENT [=] 'string'
        |======================================================
        |""".stripMargin
    )

  /** The COMPRESSION option is a feature for compressing and storing data. This reduces the size of the database and
    * saves space. Different setting values will change the compression algorithm, resulting in different compression
    * speeds and efficiencies.
    */
  private def compression: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("compression"),
        caseSensitivity("zlib") | caseSensitivity("lz4") | caseSensitivity("none")
      ) ^^ {
        case str if str.toUpperCase == "ZLIB" => Table.Options.Compression("ZLIB")
        case str if str.toUpperCase == "LZ4"  => Table.Options.Compression("LZ4")
        case str if str.toUpperCase == "NONE" => Table.Options.Compression("NONE")
      },
      """
        |======================================================
        |There is an error in the compression format.
        |Please correct the format according to the following.
        |
        |example: COMPRESSION [=] {ZLIB | LZ4 | NONE}
        |======================================================
        |""".stripMargin
    )

  /** The CONNECTION option is one of the settings used by the MySQL database. It is used to configure settings related
    * to the connection to the database.
    */
  private def connection: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("connection"), stringLiteral) ^^ Table.Options.Connection.apply,
      """
        |======================================================
        |There is an error in the connection format.
        |Please correct the format according to the following.
        |
        |scheme: The recognized connection protocol. At this time, only mysql is supported as a scheme value.
        |
        |user_name: User name for the connection. This user must have been created on the remote server and must have the appropriate privileges to perform the required actions (SELECT, INSERT, UPDATE, etc.) on the remote table.
        |
        |password: (optional) Password corresponding to user_name.
        |
        |host_name: Host name or IP address of the remote server.
        |
        |port_num: (Optional) Port number of the remote server. Default is 3306.
        |
        |db_name: Name of the database holding the remote table.
        |
        |tbl_name: Name of the remote table. The names of the local and remote tables do not need to match.
        |
        |example: CONNECTION [=] 'scheme://user_name[:password]@host_name[:port_num]/db_name/tbl_name'
        |======================================================
        |""".stripMargin
    )

  /** DATA DIRECTORY and INDEX DIRECTORY are options used in the MySQL database. These options are used to specify where
    * data and indexes are stored.
    */
  private def directory: Parser[Table.Options] =
    customError(
      keyValue(
        (caseSensitivity("data") | caseSensitivity("index")) ~> caseSensitivity("directory"),
        stringLiteral
      ) ^^ Table.Options.Directory.apply,
      """
        |======================================================
        |There is an error in the directory format.
        |Please correct the format according to the following.
        |
        |example: {DATA | INDEX} DIRECTORY [=] 'string'
        |======================================================
        |""".stripMargin
    )

  /** Specifies how to use delayed key writing. This applies only to MyISAM tables. Delayed key writes do not flush the
    * key buffer between writes.
    */
  private def delayKeyWrite: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("delay_key_write"), """(0|1)""".r) ^^ {
        case "0" => Table.Options.DelayKeyWrite(0)
        case "1" => Table.Options.DelayKeyWrite(1)
      },
      """
        |======================================================
        |There is an error in the delay_key_write format.
        |Please correct the format according to the following.
        |
        |example: DELAY_KEY_WRITE [=] {0 | 1}
        |======================================================
        |""".stripMargin
    )

  /** The ENCRYPTION option is one of the settings used in the MySQL database. It is used to encrypt (encode) data.
    */
  protected def encryption: Parser[Table.Options.Encryption] =
    customError(
      keyValue(caseSensitivity("encryption"), caseSensitivity("y") | caseSensitivity("n")) ^^ {
        case str if str.toUpperCase == "Y" => Table.Options.Encryption("Y")
        case str if str.toUpperCase == "N" => Table.Options.Encryption("N")
      },
      """
        |======================================================
        |There is an error in the encryption format.
        |Please correct the format according to the following.
        |
        |example: ENCRYPTION [=] {Y | N}
        |======================================================
        |""".stripMargin
    )

  /** Option to specify storage engine for table
    */
  private def engine: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("engine"),
        "InnoDB" | "MyISAM" | "MEMORY" | "CSV" | "ARCHIVE" | "EXAMPLE" | "FEDERATED" | "HEAP" | "MERGE" | "NDB"
      ) ^^ {
        case "InnoDB"    => Table.Options.Engine("InnoDB")
        case "MyISAM"    => Table.Options.Engine("MyISAM")
        case "MEMORY"    => Table.Options.Engine("MEMORY")
        case "CSV"       => Table.Options.Engine("CSV")
        case "ARCHIVE"   => Table.Options.Engine("ARCHIVE")
        case "EXAMPLE"   => Table.Options.Engine("EXAMPLE")
        case "FEDERATED" => Table.Options.Engine("FEDERATED")
        case "HEAP"      => Table.Options.Engine("HEAP")
        case "MERGE"     => Table.Options.Engine("MERGE")
        case "NDB"       => Table.Options.Engine("NDB")
      },
      """
        |======================================================
        |There is an error in the engine format.
        |Please correct the format according to the following.
        |
        |example: ENGINE [=] {InnoDB | MyISAM | MEMORY | CSV | ARCHIVE | EXAMPLE | FEDERATED | HEAP | MERGE | NDB}
        |======================================================
        |""".stripMargin
    )

  /** When inserting data into a MERGE table, INSERT_METHOD must be used to specify the table into which the rows are to
    * be inserted. INSERT_METHOD is a useful option only for MERGE tables.
    */
  private def insertMethod: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("insert_method"),
        caseSensitivity("NO") | caseSensitivity("FIRST") | caseSensitivity("LAST")
      ) ^^ {
        case str if str.toUpperCase == "NO"    => Table.Options.InsertMethod("NO")
        case str if str.toUpperCase == "FIRST" => Table.Options.InsertMethod("FIRST")
        case str if str.toUpperCase == "LAST"  => Table.Options.InsertMethod("LAST")
      },
      """
        |======================================================
        |There is an error in the insert_method format.
        |Please correct the format according to the following.
        |
        |example: INSERT_METHOD [=] {NO | FIRST | LAST}
        |======================================================
        |""".stripMargin
    )

  /** The maximum number of rows you plan to store in the table. This is not a strong limit, but rather a hint to the
    * storage engine that the table must be able to store at least this number of rows.
    */
  private def maxRows: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("max_rows"), """-?\d+""".r.filter(_.toLong < 4294967296L)) ^^ { digit =>
        Table.Options.MaxRows(digit.toLong)
      },
      """
        |======================================================
        |There is an error in the max_rows format.
        |Please correct the format according to the following.
        |
        |size must be less than 4294967295.
        |
        |example: MAX_ROWS [=] 'size'
        |======================================================
        |""".stripMargin
    )

  /** The minimum number of rows you plan to store in the table. MEMORY The storage engine uses this option as a hint
    * regarding memory usage.
    */
  private def minRows: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("min_rows"), """-?\d+""".r) ^^ { digit =>
        Table.Options.MinRows(digit.toLong)
      },
      """
        |======================================================
        |There is an error in the min_rows format.
        |Please correct the format according to the following.
        |
        |example: MIN_ROWS [=] 'size'
        |======================================================
        |""".stripMargin
    )

  /** Valid only for MyISAM tables. Set this option to 1 for smaller indexes. This usually results in slower updates and
    * faster reads. Setting this option to 0 disables all packing of keys. Setting it to DEFAULT tells the storage
    * engine to pack only long CHAR, VARCHAR, BINARY, or VARBINARY columns.
    */
  private def packKeys: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("pack_keys"),
        "0" | "1" | caseSensitivity("default")
      ) ^^ {
        case "0"                                 => Table.Options.PackKeys("0")
        case "1"                                 => Table.Options.PackKeys("1")
        case str if str.toUpperCase == "DEFAULT" => Table.Options.PackKeys("DEFAULT")
      },
      """
        |======================================================
        |There is an error in the pack_keys format.
        |Please correct the format according to the following.
        |
        |example: PACK_KEYS [=] {0 | 1 | DEFAULT}
        |======================================================
        |""".stripMargin
    )

  /** Defines the physical format in which the rows will be stored.
    */
  private def rowFormat: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("row_format"),
        caseSensitivity("default") | caseSensitivity("dynamic") | caseSensitivity("fixed") | caseSensitivity(
          "redundant"
        ) | caseSensitivity("compact")
      ) ^^ {
        case str if str.toUpperCase == "DEFAULT"    => Table.Options.RowFormat("DEFAULT")
        case str if str.toUpperCase == "DYNAMIC"    => Table.Options.RowFormat("DYNAMIC")
        case str if str.toUpperCase == "FIXED"      => Table.Options.RowFormat("FIXED")
        case str if str.toUpperCase == "COMPRESSED" => Table.Options.RowFormat("COMPRESSED")
        case str if str.toUpperCase == "REDUNDANT"  => Table.Options.RowFormat("REDUNDANT")
        case str if str.toUpperCase == "COMPACT"    => Table.Options.RowFormat("COMPACT")
      },
      """
        |======================================================
        |There is an error in the row_format format.
        |Please correct the format according to the following.
        |
        |example: ROW_FORMAT [=] {DEFAULT | DYNAMIC | FIXED | COMPRESSED | REDUNDANT | COMPACT}
        |======================================================
        |""".stripMargin
    )

  /** Specifies whether the persistent statistics for InnoDB tables should be automatically recalculated. With the value
    * DEFAULT, the table's persistent statistics settings are determined by the innodb_stats_auto_recalc configuration
    * option. A value of 1 specifies that the statistics will be recalculated when 10% of the data in the table has
    * changed. A value of 0 prevents automatic recalculation of this table. With this setting, to recalculate the
    * statistics after making significant changes to the table, issue an ANALYZE TABLE statement.
    */
  private def statsAutoRecalc: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("stats_auto_recalc"),
        "0" | "1" | caseSensitivity("default")
      ) ^^ {
        case "0"                                 => Table.Options.StatsAutoRecalc("0")
        case "1"                                 => Table.Options.StatsAutoRecalc("1")
        case str if str.toUpperCase == "DEFAULT" => Table.Options.StatsAutoRecalc("DEFAULT")
      },
      """
        |======================================================
        |There is an error in the stats_auto_recalc format.
        |Please correct the format according to the following.
        |
        |example: STATS_AUTO_RECALC [=] {0 | 1 | DEFAULT}
        |======================================================
        |""".stripMargin
    )

  /** Specifies whether to enable persistent statistics for InnoDB tables. With the value DEFAULT, the table persistent
    * statistics setting is determined by the innodb_stats_persistent configuration option. A value of 1 enables
    * persistent statistics for the table, while a value of 0 disables this feature.
    */
  private def statsPersistent: Parser[Table.Options] =
    customError(
      keyValue(
        caseSensitivity("stats_persistent"),
        "0" | "1" | caseSensitivity("default")
      ) ^^ {
        case "0"                                 => Table.Options.StatsPersistent("0")
        case "1"                                 => Table.Options.StatsPersistent("1")
        case str if str.toUpperCase == "DEFAULT" => Table.Options.StatsPersistent("DEFAULT")
      },
      """
        |======================================================
        |There is an error in the stats_persistent format.
        |Please correct the format according to the following.
        |
        |example: STATS_PERSISTENT [=] {0 | 1 | DEFAULT}
        |======================================================
        |""".stripMargin
    )

  /** Number of index pages to sample when estimating cardinality and other statistics (such as those computed by
    * ANALYZE TABLE) for indexed columns.
    */
  private def statsSamplePages: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("stats_sample_pages"), digit) ^^ Table.Options.StatsSamplePages.apply,
      """
        |======================================================
        |There is an error in the stats_sample_pages format.
        |Please correct the format according to the following.
        |
        |example: STATS_SAMPLE_PAGES [=] 'size'
        |======================================================
        |""".stripMargin
    )

  /** The TABLESPACE clause can be used to create tables in an existing general tablespace, file-per-table tablespace,
    * or system tablespace.
    */
  private def tablespace: Parser[Table.Options] =
    customError(
      caseSensitivity("tablespace") ~> sqlIdent ~ opt(
        caseSensitivity("storage") ~> caseSensitivity("disk") | caseSensitivity("memory")
      ) ^^ {
        case name ~ storage =>
          Table.Options.Tablespace(
            name,
            storage.map {
              case "DISK"   => "DISK"
              case "MEMORY" => "MEMORY"
            }
          )
      },
      """
        |======================================================
        |There is an error in the tablespace format.
        |Please correct the format according to the following.
        |
        |example: TABLESPACE [=] 'string' [STORAGE {DISK | MEMORY}]
        |======================================================
        |""".stripMargin
    )

  /** Used to access collections of identical MyISAM tables. This only works with MERGE tables.
    */
  private def union: Parser[Table.Options] =
    customError(
      keyValue(caseSensitivity("union"), "(" ~> repsep(sqlIdent, ",") <~ ")") ^^ Table.Options.Union.apply,
      """
        |======================================================
        |There is an error in the union format.
        |Please correct the format according to the following.
        |
        |example: UNION [=] (table_name, table_name, ...)
        |======================================================
        |""".stripMargin
    )

  private def tableOption: Parser[Table.Options] =
    autoextendSize | autoIncrement | avgRowLength | characterSet | checksum | collateSet |
      commentOption | compression | connection | directory | delayKeyWrite | encryption |
      engine | engineAttribute ^^ Table.Options.EngineAttribute.apply | insertMethod |
      keyBlockSize ^^ Table.Options.KeyBlockSize.apply | maxRows | minRows | packKeys |
      rowFormat | keyValue(
        caseSensitivity("secondary_engine_attribute"),
        sqlIdent
      ) ^^ Table.Options.SecondaryEngineAttribute.apply |
      statsAutoRecalc | statsPersistent | statsSamplePages | tablespace | union

  protected def tableStatements: Parser[Table.CreateStatement | Table.DropStatement] = createStatement | dropStatement

  /** Parser for parsing Table create statement.
    *
    * Please refer to the official documentation for MySQL Table create statement. SEE:
    * https://dev.mysql.com/doc/refman/8.0/en/create-table.html
    */
  private def createStatement: Parser[Table.CreateStatement] =
    opt(comment) ~> create ~> opt(comment) ~> opt(temporary) ~> opt(comment) ~> table ~>
      opt(comment) ~> opt(ifNotExists) ~> opt(comment) ~> sqlIdent ~ opt(comment) ~
      "(" ~ repsep(columnDefinition | keyDefinitions, ",") ~ opt(comment) ~ ")" ~ opt(rep1(tableOption)) <~ ";" ^^ {
        case tableName ~ _ ~ _ ~ objects ~ _ ~ _ ~ options =>
          val columnDefs = objects.filter(_.isInstanceOf[ColumnDefinition]).asInstanceOf[List[ColumnDefinition]]
          val keyDefs    = objects.filter(_.isInstanceOf[Key]).asInstanceOf[List[Key]]
          Table.CreateStatement(tableName, columnDefs, keyDefs, options)
      }

  /** Parser for parsing Table drop statement.
    *
    * Please refer to the official documentation for MySQL Table drop statement. SEE:
    * https://dev.mysql.com/doc/refman/8.0/en/drop-table.html
    */
  private def dropStatement: Parser[Table.DropStatement] =
    customError(
      opt(comment) ~> drop ~> opt(comment) ~> opt(ifNotExists) ~> opt(comment) ~> sqlIdent <~ opt(comment) <~ ";" ^^ {
        tableName => Table.DropStatement(tableName)
      },
      """
        |======================================================
        |There is an error in the drop statement format.
        |Please correct the format according to the following.
        |
        |example: DROP [IF NOT EXISTS] `table_name`;
        |======================================================
        |""".stripMargin
    )
