package com.painkillergis.fall_color_history.history

class HistoryService {
  private var history = emptyList<Map<String, Any>>()

  fun get(): List<Map<String, Any>> = history

  fun notify(update: Map<String, Any>) {
    history = if (history.isEmpty() || history.last() != update) {
      history + update
    } else history
  }

  fun clear() {
    history = emptyList()
  }
}