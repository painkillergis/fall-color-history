package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun <R> withTestController(controller: Application.() -> Unit, block: TestApplicationEngine.() -> R) =
  withTestApplication({
    globalModules()
    controller()
  }, block)

class LatestControllerKtTest : FunSpec({
  test("latest is the same") {
    withTestController({ latestController() }) {
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!) shouldBe mapOf(
          "the" to "latest",
        )
      }
    }
  }
})