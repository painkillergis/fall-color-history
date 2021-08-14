package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.history.HistoryService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify

class LatestServiceTest : FunSpec({
  val historyService = mockk<HistoryService>(relaxed = true)
  val latestService = LatestService(historyService)

  test("default latest") {
    latestService.get() shouldBe emptyMap()
  }

  test("put latest and notify history") {
    val latest = mapOf("the" to "late latest")
    latestService.put(latest)

    latestService.get() shouldBe latest
    verify { historyService.notify(latest) }
  }

  test("clear") {
    latestService.put(mapOf("the" to "update to clear"))
    latestService.clear()

    latestService.get() shouldBe emptyMap()
  }
})
