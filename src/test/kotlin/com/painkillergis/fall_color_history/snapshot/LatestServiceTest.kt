package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify

class LatestServiceTest : FunSpec({
  val historyService = mockk<HistoryService>(relaxed = true)
  val latestService = LatestService(historyService, Database())

  afterEach {
    latestService.clear()
  }

  test("default latest") {
    latestService.get() shouldBe emptyMap()
  }

  test("put latest and notify history") {
    val latest = mapOf("the" to "late latest")
    latestService.put(latest)

    latestService.get() shouldBe latest.toJsonElement()
    verify { historyService.notify(latest) }
  }

  test("replace latest") {
    val oldest = mapOf("the" to "old oldest")
    val latest = mapOf("the" to "late latest")
    latestService.put(oldest)
    latestService.put(latest)

    latestService.get() shouldBe latest.toJsonElement()
  }

  test("clear") {
    latestService.put(mapOf("the" to "update to clear"))
    latestService.clear()

    latestService.get() shouldBe emptyMap()
  }
})
