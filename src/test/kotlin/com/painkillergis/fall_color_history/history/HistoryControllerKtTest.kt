package com.painkillergis.fall_color_history.history

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.slf4j.Logger

class HistoryControllerKtTest : FunSpec({
  val historyService = mockk<HistoryService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  fun <R> withTestController(block: TestApplicationEngine.() -> R) =
    withTestApplication({
      globalModules()
      historyController(historyService, log)
    }, block)

  test("get history") {
    withTestController {
      val history = emptyList<Map<String, Any>>()
      every { historyService.get() } returns history
      handleRequest(HttpMethod.Get, "/history").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<JsonObject>(response.content!!) shouldBe mapOf("history" to history)
      }
    }
  }

  test("get history has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { historyService.get() } throws exception
      handleRequest(HttpMethod.Get, "/history").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error getting history", exception) }
    }
  }

  test("delete history") {
    withTestController {
      handleRequest(HttpMethod.Delete, "/history").apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("delete history has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { historyService.clear() } throws exception
      handleRequest(HttpMethod.Delete, "/history").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error clearing history", exception) }
    }
  }
})