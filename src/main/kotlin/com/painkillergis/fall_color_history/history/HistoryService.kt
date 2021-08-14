package com.painkillergis.fall_color_history.history

class HistoryService {
  private var history = emptyList<Map<String, Any>>()

  fun get(): Map<String, Any> = mapOf("history" to history)

  fun notify(update: Map<String, Any>) {
    history = if (history.isEmpty() || history.last() != update) {
      history + update
    } else history
  }

  fun clear() {
    history = emptyList()
  }
}