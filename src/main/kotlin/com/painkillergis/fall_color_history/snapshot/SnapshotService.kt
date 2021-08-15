package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.sql.ResultSet

class SnapshotService(
  private val database: Database,
) {
  init {
    database.useStatement {
      execute("create table if not exists history (document text)")
    }
  }

  fun getHistory(): List<Map<String, Any>> = database.useStatement {
    executeQuery("select * from history order by rowid")
      .let(::deserializeResultSet)
  }

  fun getLatest(): Map<String, Any> = database.useStatement {
    executeQuery("select * from history order by rowid desc limit 1")
      .let(::deserializeResultSet)
      .firstOrNull() ?: emptyMap()
  }

  private fun deserializeResultSet(resultSet: ResultSet) =
    mutableListOf<JsonObject>().apply {
      while (resultSet.next()) {
        add(Json.decodeFromString(resultSet.getString(1)))
      }
    }

  fun replaceLatest(latest: Map<String, Any>) = database.useConnection {
    if (getLatest() != latest.toJsonElement()) {
      prepareStatement("insert into history (document) values (?)").use {
        it.setString(1, Json.encodeToString(latest.toJsonElement()))
        it.executeUpdate()
      }
    }
  }

  fun clear() = database.useStatement { execute("delete from history") }
}