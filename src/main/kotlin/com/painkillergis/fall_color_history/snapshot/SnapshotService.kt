package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import com.painkillergis.fall_color_history.util.toJsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.ResultSet

class SnapshotService(
  private val database: Database = Database(),
  private val timestampService: TimestampService = TimestampService(),
) {
  init {
    database.useStatement {
      execute("create table if not exists history (document text, timestamp text)")
    }
  }

  fun getHistory(): List<SnapshotContainer> = database.useStatement {
    executeQuery("select document, timestamp from history order by rowid")
      .let(::deserializeResultSet)
  }

  fun getLatest(): SnapshotContainer = database.useStatement {
    executeQuery("select document, timestamp from history order by rowid desc limit 1")
      .let(::deserializeResultSet)
      .firstOrNull() ?: SnapshotContainer()
  }

  private fun deserializeResultSet(resultSet: ResultSet) =
    mutableListOf<SnapshotContainer>().apply {
      while (resultSet.next()) {
        add(
          SnapshotContainer(
            resultSet.getString(2),
            Json.decodeFromString(resultSet.getString(1)),
          )
        )
      }
    }

  fun replaceLatest(latest: Map<String, Any>) = database.useConnection {
    val previousWithoutPhoto = getLatest().content.filterKeys { it != "photo" }
    val nextWithoutPhoto = latest.toJsonObject().filterKeys { it != "photo" }
    if (previousWithoutPhoto != nextWithoutPhoto) {
      prepareStatement("insert into history (document, timestamp) values (?, ?)").use {
        it.setString(1, Json.encodeToString(latest.toJsonElement()))
        it.setString(2, timestampService.getTimestamp())
        it.executeUpdate()
      }
    }
  }

  fun clear() = database.useStatement { execute("delete from history") }
}