package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LatestControllerKtTest : FunSpec({
  test("latest is the same") {
    withTestApplication({
      globalModules()
      latestController()
    }) {
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!) shouldBe mapOf(
          "the" to "latest",
        )
      }
    }
  }
})