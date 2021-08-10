package com.painkillergis.fall_color_history.latest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LatestServiceTest : FunSpec({
  val latestService = LatestService()

  test("default latest") {
    latestService.get() shouldBe emptyMap()
  }

  test("put latest") {
    val jsonObject = buildJsonObject { put("the", "late latest") }

    latestService.apply {
      put(jsonObject)
      get() shouldBe jsonObject
    }
  }

  test("clear") {
    latestService.put(buildJsonObject { put("the", "update to clear") })

    latestService.clear()

    latestService.get() shouldBe emptyMap()
  }
})
