package com.painkillergis.fall_color_history.history

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class HistoryServiceTest : FunSpec({
  val historyService = HistoryService()

  afterEach { historyService.clear() }

  test("default history") {
    historyService.get() shouldBe emptyList<Map<String, Any>>()
  }

  test("history after updates") {
    historyService.notify(mapOf("the" to "first update"))
    historyService.notify(mapOf("the" to "second update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "first update"),
      mapOf("the" to "second update"),
    )
  }

  test("discard duplicate updates") {
    historyService.notify(mapOf("the" to "same update"))
    historyService.notify(mapOf("the" to "same update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "same update"),
    )
  }

  test("preserve non-sequential duplicate updates") {
    historyService.notify(mapOf("the" to "same update"))
    historyService.notify(mapOf("the" to "different update"))
    historyService.notify(mapOf("the" to "same update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "same update"),
      mapOf("the" to "different update"),
      mapOf("the" to "same update"),
    )
  }

  test("clear history") {
    historyService.notify(mapOf("the" to "update"))
    historyService.clear()

    historyService.get() shouldBe emptyList()
  }
})
