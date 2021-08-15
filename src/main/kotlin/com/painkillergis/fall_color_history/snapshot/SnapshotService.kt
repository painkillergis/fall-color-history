package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class SnapshotService(
  private val database: Database,
) {
  init {
    database.useStatement {
      execute("create table if not exists history (document text)")
    }
  }

  fun clear() = database.useStatement { execute("delete from history") }

  fun getLatest(): Map<String, Any> = database.useStatement { getHistory().lastOrNull() ?: emptyMap() }

  fun replaceLatest(latest: Map<String, Any>) = database.useConnection {
    if (getLatest() != latest.toJsonElement()) {
      prepareStatement("insert into history (document) values (?)").use {
        it.setString(1, Json.encodeToString(latest.toJsonElement()))
        it.executeUpdate()
      }
    }
  }

  fun getHistory(): List<Map<String, Any>> = database.useStatement {
    val results = mutableListOf<JsonObject>()
    val resultSet = executeQuery("select * from history order by rowid")
    while (resultSet.next()) {
      results.add(Json.decodeFromString(resultSet.getString(1)))
    }
    results
  }
}