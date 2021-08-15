package com.painkillergis.fall_color_history

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class Database(
  connectionString: String = System.getenv("SQLITE_CONNECTION") ?: "jdbc:sqlite::memory:",
) {
  private val connection = DriverManager.getConnection(connectionString)

  fun <T> useConnection(block: Connection.() -> T) = block(connection)

  fun <T> useStatement(block: Statement.() -> T) =
    useConnection { createStatement().use(block) }
}