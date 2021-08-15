package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HistoryServiceTest : FunSpec({
  val historyService = HistoryService(Database())

  afterEach { historyService.clear() }

  test("default history") {
    historyService.get() shouldBe emptyList()
  }

  test("history after updates") {
    historyService.notify(mapOf("the" to "first update"))
    historyService.notify(mapOf("the" to "second update"))
    historyService.notify(mapOf("the" to "third update"))
    historyService.notify(mapOf("the" to "fourth update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "first update"),
      mapOf("the" to "second update"),
      mapOf("the" to "third update"),
      mapOf("the" to "fourth update"),
    ).map { it.toJsonElement() }
  }

  test("discard duplicate updates") {
    historyService.notify(mapOf("the" to "same update"))
    historyService.notify(mapOf("the" to "same update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "same update"),
    ).map { it.toJsonElement() }
  }

  test("preserve non-sequential duplicate updates") {
    historyService.notify(mapOf("the" to "same update"))
    historyService.notify(mapOf("the" to "different update"))
    historyService.notify(mapOf("the" to "same update"))

    historyService.get() shouldBe listOf(
      mapOf("the" to "same update"),
      mapOf("the" to "different update"),
      mapOf("the" to "same update"),
    ).map { it.toJsonElement() }
  }

  test("clear history") {
    historyService.notify(mapOf("the" to "update"))
    historyService.clear()

    historyService.get() shouldBe emptyList()
  }
})
