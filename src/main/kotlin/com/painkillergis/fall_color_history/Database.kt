package com.painkillergis.fall_color_history

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

object Database {
  private val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

  fun <T> useConnection(block: Connection.() -> T) = block(connection)

  fun <T> useStatement(block: Statement.() -> T) =
    useConnection { createStatement().use(block) }
}