package com.painkillergis.fall_color_history.history

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class HistoryService {
  private var jsonArray = buildJsonArray { }

  fun get(): JsonObject = buildJsonObject {
    put("history", jsonArray)
  }

  fun notify(update: JsonObject) {
    jsonArray = buildJsonArray {
      jsonArray.forEach(::add)
      if (jsonArray.isEmpty() || jsonArray.last() != update) {
        add(update)
      }
    }
  }

  fun clear() {
    jsonArray = buildJsonArray { }
  }
}