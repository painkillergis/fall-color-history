package com.painkillergis.fall_color_history.latest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LatestServiceTest : FunSpec({
  test("default latest") {
    LatestService().get() shouldBe emptyMap()
  }

  test("put latest") {
    val jsonObject = buildJsonObject { put("the", "late latest") }

    LatestService().apply {
      put(jsonObject)
      get() shouldBe jsonObject
    }
  }
})
