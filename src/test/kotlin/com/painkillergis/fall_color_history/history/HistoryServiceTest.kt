package com.painkillergis.fall_color_history.history

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject

class HistoryServiceTest : FunSpec({
  test("default history") {
    HistoryService().get() shouldBe buildJsonObject { }
  }
})
