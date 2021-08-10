package com.painkillergis.fall_color_history.latest

import kotlinx.serialization.json.JsonObject

class LatestService {
  private var state = emptyMap<String, Any>()

  fun get() = state

  fun put(next: JsonObject) {
    state = next
  }
}
