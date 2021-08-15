package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.BFunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class LatestBTest : BFunSpec({ httpClient ->
  afterEach {
    httpClient.delete<Unit>("/latest")
    httpClient.delete<Unit>("/history")
  }

  test("no latest") {
    httpClient.get<HttpResponse>("/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe emptyMap()
    }
  }

  test("put latest") {
    val next = buildJsonObject { put("locations", buildJsonArray { }) }
    httpClient.put<HttpResponse>("/latest") {
      contentType(ContentType.Application.Json)
      body = next
    }.apply {
      status shouldBe HttpStatusCode.NoContent
    }

    httpClient.get<HttpResponse>("/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe next
    }
  }
})