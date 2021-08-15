package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.history.HistoryService
import com.painkillergis.fall_color_history.util.toJsonElement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class LatestService(
  private val historyService: HistoryService,
  private val database: Database,
) {
  init {
    database.useStatement {
      execute("create table if not exists latest (id integer primary key, text content)")
    }
  }

  fun get(): Map<String, Any> = database.useStatement { getAll().firstOrNull() ?: emptyMap() }

  private fun getAll() = database.useStatement {
    val results = mutableListOf<JsonObject>()
    val resultSet = executeQuery("select * from latest")
    while (resultSet.next()) {
      results.add(Json.decodeFromString(resultSet.getString(2)))
    }
    results
  }

  fun put(next: Map<String, Any>) {
    val text = Json.encodeToString(next.toJsonElement())
    database.useConnection {
      prepareStatement("insert into latest (id, text) values (0, ?) on conflict do update set text = ?").use {
        it.setString(1, text)
        it.setString(2, text)
        it.executeUpdate()
      }
    }
    historyService.notify(next)
  }

  fun clear() = database.useStatement { execute("delete from latest") }
}
