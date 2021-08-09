package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.util.withConfiguredTestApplication
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LatestBTest : FunSpec({
  test("latest is the same") {
    withConfiguredTestApplication {
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!) shouldBe mapOf(
          "the" to "latest",
        )
      }
    }
  }
})