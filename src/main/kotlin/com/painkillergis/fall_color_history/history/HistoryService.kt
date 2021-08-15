package com.painkillergis.fall_color_history.history

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class HistoryService(private val database: Database) {
  init {
    database.useStatement {
      execute("create table if not exists history (document text)")
    }
  }

  fun get(): List<Map<String, Any>> = database.useStatement {
    val results = mutableListOf<JsonObject>()
    val resultSet = executeQuery("select * from history")
    while (resultSet.next()) {
      results.add(Json.decodeFromString(resultSet.getString(1)))
    }
    results
  }

  fun notify(update: Map<String, Any>) {
    if (getLast() == update.toJsonElement()) return
    database.useConnection {
      prepareStatement("insert into history (document) values (?)").use {
        it.setString(1, Json.encodeToString(update.toJsonElement()))
        it.executeUpdate()
      }
    }
  }

  private fun getLast(): JsonObject? = database.useStatement {
    val resultSet = executeQuery("select * from history order by rowid desc limit 1")
    if (resultSet.next()) Json.decodeFromString(resultSet.getString(1)) else null
  }

  fun clear() = database.useStatement { execute("delete from history") }
}