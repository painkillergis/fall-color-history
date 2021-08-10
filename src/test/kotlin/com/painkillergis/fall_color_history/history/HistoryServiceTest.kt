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
    historyService.get() shouldBe buildJsonObject {
      putJsonArray("history") {}
    }
  }

  test("history after updates") {
    historyService.notify(
      buildJsonObject {
        put("the", "first update")
      }
    )

    historyService.notify(
      buildJsonObject {
        put("the", "second update")
      }
    )

    historyService.get() shouldBe buildJsonObject {
      putJsonArray("history") {
        add(
          buildJsonObject {
            put("the", "first update")
          }
        )
        add(
          buildJsonObject {
            put("the", "second update")
          }
        )
      }
    }
  }

  test("discard duplicate updates") {
    historyService.notify(
      buildJsonObject {
        put("the", "same update")
      }
    )

    historyService.notify(
      buildJsonObject {
        put("the", "same update")
      }
    )

    historyService.get() shouldBe buildJsonObject {
      putJsonArray("history") {
        add(
          buildJsonObject {
            put("the", "same update")
          }
        )
      }
    }
  }

  test("preserve non-sequential duplicate updates") {
    historyService.notify(
      buildJsonObject {
        put("the", "same update")
      }
    )

    historyService.notify(
      buildJsonObject {
        put("the", "different update")
      }
    )

    historyService.notify(
      buildJsonObject {
        put("the", "same update")
      }
    )

    historyService.get() shouldBe buildJsonObject {
      putJsonArray("history") {
        add(
          buildJsonObject {
            put("the", "same update")
          }
        )
        add(
          buildJsonObject {
            put("the", "different update")
          }
        )
        add(
          buildJsonObject {
            put("the", "same update")
          }
        )
      }
    }
  }

  test("clear history") {
    historyService.notify(
      buildJsonObject {
        put("the", "update")
      }
    )

    historyService.clear()

    historyService.get() shouldBe buildJsonObject {
      putJsonArray("history") { }
    }
  }
})
