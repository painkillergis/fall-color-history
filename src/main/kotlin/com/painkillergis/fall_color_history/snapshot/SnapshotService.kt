package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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

  fun getLatestSnapshot(): SnapshotContainer = database.useStatement {
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

  fun replaceLatest(latest: LocationsContainer) = database.useConnection {
    if (getLatestSnapshot().content notEqualsIgnoringPhotos latest) {
      prepareStatement("insert into history (document, timestamp) values (?, ?)").use {
        it.setString(1, Json.encodeToString(latest))
        it.setString(2, timestampService.getTimestamp())
        it.executeUpdate()
      }
    }
  }

  private infix fun LocationsContainer.notEqualsIgnoringPhotos(other: LocationsContainer) =
    locations.map(::filterNotPhotos) != other.locations.map(::filterNotPhotos)

  private fun filterNotPhotos(location: JsonElement) =
    (location as JsonObject).filterKeys { it != "photo" }.toJsonObject()

  fun clear() = database.useStatement { execute("delete from history") }
}