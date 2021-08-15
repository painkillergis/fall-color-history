package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.globalModules
import com.painkillergis.fall_color_history.util.toJsonElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.Logger

class LatestControllerKtTest : FunSpec({
  val snapshotService = mockk<SnapshotService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  fun <R> withTestController(block: TestApplicationEngine.() -> R) =
    withTestApplication({
      globalModules()
      latestController(snapshotService, log)
    }, block)

  test("get latest") {
    withTestController {
      val latest = mapOf("the" to JsonPrimitive("late latest"))
      every { snapshotService.getLatest() } returns latest
      handleRequest(HttpMethod.Get, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<JsonObject>(response.content!!) shouldBe latest
      }
    }
  }

  test("get latest has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { snapshotService.getLatest() } throws exception
      handleRequest(HttpMethod.Get, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error getting the latest", exception) }
    }
  }

  test("replace latest") {
    withTestController {
      val latest = mapOf("the" to "late late latest")

      handleRequest(HttpMethod.Put, "/snapshots/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(latest))
      }.apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }

      verify { snapshotService.replaceLatest(latest.toJsonElement() as JsonObject) }
    }
  }

  test("replace latest has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { snapshotService.replaceLatest(any()) } throws exception

      handleRequest(HttpMethod.Put, "/snapshots/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(emptyMap<String, Unit>()))
      }.apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error setting the latest", exception) }
    }
  }
})