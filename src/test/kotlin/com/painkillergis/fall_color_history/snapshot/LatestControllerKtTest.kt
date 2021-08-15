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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.Logger

class LatestControllerKtTest : FunSpec({
  val latestService = mockk<LatestService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  fun <R> withTestController(block: TestApplicationEngine.() -> R) =
    withTestApplication({
      globalModules()
      latestController(latestService, log)
    }, block)

  test("get latest") {
    withTestController {
      val latest = mapOf("the" to JsonPrimitive("late latest"))
      every { latestService.get() } returns latest
      handleRequest(HttpMethod.Get, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<JsonObject>(response.content!!) shouldBe latest
      }
    }
  }

  test("get latest has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { latestService.get() } throws exception
      handleRequest(HttpMethod.Get, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error getting the latest", exception) }
    }
  }

  test("replace latest") {
    withTestController {
      val latest = mapOf("the" to JsonPrimitive("late late latest"))

      handleRequest(HttpMethod.Put, "/snapshots/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(latest))
      }.apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }

      verify { latestService.put(latest) }
    }
  }

  test("replace latest has error") {
    withTestController {
      val exception = RuntimeException("the message")
      every { latestService.put(any()) } throws exception

      handleRequest(HttpMethod.Put, "/snapshots/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(emptyMap<String, JsonElement>()))
      }.apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error setting the latest", exception) }
    }
  }

  test("clear latest") {
    withTestController {
      handleRequest(HttpMethod.Delete, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("clear latest has error") {
    val exception = RuntimeException("the message")
    every { latestService.clear() } throws exception
    withTestController {
      handleRequest(HttpMethod.Delete, "/snapshots/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error clearing the latest", exception) }
    }
  }

})