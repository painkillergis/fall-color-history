package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger

fun <R> withTestController(controller: Application.() -> Unit, block: TestApplicationEngine.() -> R) =
  withTestApplication({
    globalModules()
    controller()
  }, block)

class LatestControllerKtTest : FunSpec({
  val latestService = mockk<LatestService>(relaxed = true)
  val log = mockk<Logger>(relaxed = true)

  test("latest from service") {
    withTestController({ latestController(latestService, log) }) {
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
    withTestController({ latestController(latestService, log) }) {
      val exception = RuntimeException("the message")
      every { latestService.get() } throws exception
      handleRequest(HttpMethod.Get, "/latest").apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error getting the latest", exception) }
    }
  }

  test("put to service with next latest") {
    withTestController({ latestController(latestService, log) }) {
      handleRequest(HttpMethod.Put, "/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(mapOf("the" to "late late latest")))
      }.apply {
        response.status() shouldBe HttpStatusCode.NoContent
      }
    }
  }

  test("put to service has error") {
    val exception = RuntimeException("the message")
    every { latestService.put(mapOf("the" to "late late latest")) } throws exception
    withTestController({ latestController(latestService, log) }) {
      handleRequest(HttpMethod.Put, "/latest") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(mapOf("the" to "late late latest")))
      }.apply {
        response.status() shouldBe HttpStatusCode.InternalServerError
      }
      verify { log.error("There was an error setting the latest", exception) }
    }
  }
})