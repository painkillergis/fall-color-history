package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.history.HistoryService
import kotlinx.serialization.json.JsonObject

class LatestService(
  private val historyService: HistoryService,
) {
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
