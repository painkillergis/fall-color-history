package com.painkillergis.fall_color_history.snapshot

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
  val snapshotService = mockk<SnapshotService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  fun <R> withTestController(block: TestApplicationEngine.() -> R) =
    withTestApplication({
      globalModules()
      historyController(snapshotService, log)
    }, block)

  test("get history") {
    withTestController {
      val history = emptyList<Map<String, Any>>()
      every { snapshotService.getHistory() } returns history
      handleRequest(HttpMethod.Get, "/snapshots").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<JsonObject>(response.content!!) shouldBe mapOf("history" to history)
      }
    }
  }

  test("get history has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { snapshotService.getHistory() } throws exception
      handleRequest(HttpMethod.Get, "/snapshots").response.status() shouldBe HttpStatusCode.InternalServerError
      verify { log.error("There was an error getting history", exception) }
    }
  }

  test("delete snapshots") {
    withTestController {
      handleRequest(HttpMethod.Delete, "/snapshots").response.status() shouldBe HttpStatusCode.NoContent
    }
  }

  test("delete snapshots has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { snapshotService.clear() } throws exception
      handleRequest(HttpMethod.Delete, "/snapshots").response.status() shouldBe HttpStatusCode.InternalServerError
      verify { log.error("There was an error clearing history", exception) }
    }
  }
})