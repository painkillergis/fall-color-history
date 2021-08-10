package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.history.HistoryService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LatestServiceTest : FunSpec({
  val historyService = mockk<HistoryService>(relaxed = true)
  val latestService = LatestService(historyService)

  test("default latest") {
    latestService.get() shouldBe emptyMap()
  }

  test("put latest and notify history") {
    val jsonObject = buildJsonObject { put("the", "late latest") }

    latestService.apply {
      put(jsonObject)
      get() shouldBe jsonObject
    }

    verify { historyService.notify(jsonObject) }
  }

  test("clear") {
    latestService.put(buildJsonObject { put("the", "update to clear") })

    latestService.clear()

    latestService.get() shouldBe emptyMap()
  }
})
