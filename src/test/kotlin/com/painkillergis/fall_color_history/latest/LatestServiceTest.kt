package com.painkillergis.fall_color_history.latest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LatestServiceTest : FunSpec({
  test("default latest") {
    LatestService().get() shouldBe emptyMap()
  }

  test("put latest") {
    LatestService().apply {
      put(mapOf("the" to "late latest"))
      get() shouldBe mapOf("the" to "late latest")
    }
  }
})