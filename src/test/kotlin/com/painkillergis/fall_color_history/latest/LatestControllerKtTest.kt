package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun <R> withTestController(controller: Application.() -> Unit, block: TestApplicationEngine.() -> R) =
  withTestApplication({
    globalModules()
    controller()
  }, block)

class LatestControllerKtTest : FunSpec({
  val latestService = mockk<LatestService>(relaxed = true)

  test("latest from service") {
    withTestController({ latestController(latestService) }) {
      every { latestService.get() } returns mapOf("the" to "late latest")
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!) shouldBe mapOf(
          "the" to "late latest",
        )
      }
    }
  }

  test("latest from service has error") {
    withTestController({ latestController(latestService) }) {
      every { latestService.get() } throws RuntimeException("the message")
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
    }
  }

  test("put to service") {
    withTestController({ latestController(latestService) }) {
      handleRequest(HttpMethod.Put, "/latest").apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("put to service has error") {
    every { latestService.put(mapOf("the" to "late latest")) } throws RuntimeException("the message")
    withTestController({ latestController(latestService) }) {
      handleRequest(HttpMethod.Put, "/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
    }
  }
})