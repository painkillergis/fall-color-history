package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.history.HistoryService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class LatestService(
  private val historyService: HistoryService,
) {

  private val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

  private fun <T> useConnection(block: Connection.() -> T) = block(connection)

  private fun <T> useStatement(block: Statement.() -> T) =
    useConnection { createStatement().use(block) }

  init {
    useStatement {
      execute("create table if not exists latest (text content)")
    }
  }

  fun get(): Map<String, Any> = useStatement { getAll().firstOrNull() ?: emptyMap() }

  private fun getAll() = useStatement {
    val results = mutableListOf<JsonObject>()
    val resultSet = executeQuery("select * from latest")
    while (resultSet.next()) {
      results.add(Json.decodeFromString(resultSet.getString(1)))
    }
    results
  }

  fun put(next: Map<String, Any>) {
    useConnection {
      prepareStatement("insert into latest (text) values (?)").use {
        it.setString(1, Json.encodeToString(next))
        it.executeUpdate()
      }
    }
    historyService.notify(next)
  }

  fun clear() = useStatement { execute("delete from latest") }
}
