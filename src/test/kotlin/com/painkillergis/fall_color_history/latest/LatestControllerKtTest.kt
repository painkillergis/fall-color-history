package com.painkillergis.fall_color_history.latest

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
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.slf4j.Logger

class LatestControllerKtTest : FunSpec({
  val latestService = mockk<LatestService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  fun <R> withTestController(block: TestApplicationEngine.() -> R) =
    withTestApplication({
      globalModules()
      latestController(latestService, log)
    }, block)

  test("latest from service") {
    withTestController {
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
    withTestController {
      val exception = RuntimeException("the message")
      every { latestService.get() } throws exception
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error getting the latest", exception) }
    }
  }

  test("put to service with next latest") {
    withTestController {
      handleRequest(HttpMethod.Put, "/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Json.encodeToString(
            buildJsonObject {
              put("locations", buildJsonArray { })
            }
          )
        )
      }.apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("put to service has error") {
    val jsonObject = buildJsonObject { put("the", "late late latest") }
    val exception = RuntimeException("the message")
    every { latestService.put(jsonObject) } throws exception
    withTestController {
      handleRequest(HttpMethod.Put, "/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(jsonObject))
      }.apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error setting the latest", exception) }
    }
  }

  test("clear latest") {
    withTestController {
      handleRequest(HttpMethod.Delete, "/latest").apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("clear latest has error") {
    val exception = RuntimeException("the message")
    every { latestService.clear() } throws exception
    withTestController {
      handleRequest(HttpMethod.Delete, "/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error clearing the latest", exception) }
    }
  }

})