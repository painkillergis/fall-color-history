package com.painkillergis.fall_color_history.latest

class LatestService {
  private var state = emptyMap<String, Any>()

  fun get() = state

  fun put() {
    state = mapOf("the" to "latest")
  }
}
