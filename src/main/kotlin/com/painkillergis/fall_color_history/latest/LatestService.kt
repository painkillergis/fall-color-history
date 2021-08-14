package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.Database.useConnection
import com.painkillergis.fall_color_history.Database.useStatement
import com.painkillergis.fall_color_history.history.HistoryService
import com.painkillergis.fall_color_history.util.toJsonElement
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
  init {
    useStatement {
      execute("create table if not exists latest (id integer primary key, text content)")
    }
  }

  fun get(): Map<String, Any> = useStatement { getAll().firstOrNull() ?: emptyMap() }

  private fun getAll() = useStatement {
    val results = mutableListOf<JsonObject>()
    val resultSet = executeQuery("select * from latest")
    while (resultSet.next()) {
      results.add(Json.decodeFromString(resultSet.getString(2)))
    }
    results
  }

  fun put(next: Map<String, Any>) {
    val text = Json.encodeToString(next.toJsonElement())
    useConnection {
      prepareStatement("insert into latest (id, text) values (0, ?) on conflict do update set text = ?").use {
        it.setString(1, text)
        it.setString(2, text)
        it.executeUpdate()
      }
    }
    historyService.notify(next)
  }

  fun clear() = useStatement { execute("delete from latest") }
}
