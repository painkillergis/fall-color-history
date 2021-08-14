package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.history.HistoryService
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.Database

class LatestService(
  private val historyService: HistoryService,
) {
  init {
    Database.connect("jdbc:sqlite:file:fall_color_history?mode=memory&cache=shared", "org.sqlite.JDBC")
  }

  private var state = emptyMap<String, Any>()

  fun get() = state

  fun put(next: Map<String, Any>) {
    state = next
    historyService.notify(next)
  }

  fun clear() {
    state = emptyMap()
  }
}
