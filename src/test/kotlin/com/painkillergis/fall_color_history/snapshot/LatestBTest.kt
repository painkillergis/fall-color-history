package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.BFunSpec
import com.painkillergis.fall_color_history.util.toJsonObject
import io.kotest.matchers.date.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LatestBTest : BFunSpec({ httpClient ->
  afterEach {
    httpClient.delete<Unit>("/snapshots")
  }

  test("no latest") {
    httpClient.get<HttpResponse>("/snapshots/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe mapOf(
        "timestamp" to "",
        "content" to emptyMap<String, Unit>(),
      ).toJsonObject()
    }
  }

  test("replace latest") {
    val latest = mapOf("locations" to emptyList<Unit>())
    httpClient.put<HttpResponse>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = latest
    }.apply {
      status shouldBe HttpStatusCode.NoContent
    }

    httpClient.get<HttpResponse>("/snapshots/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>().apply {
        (get("timestamp") as JsonPrimitive)
          .content
          .let { Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it)) }
          .shouldBeBetween(
            Instant.now().minusSeconds(5),
            Instant.now().plusSeconds(5),
          )
        get("content") shouldBe latest
      }
    }
  }
})