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
    httpClient.delete<Unit>("/snapshots")
  }

  test("no latest") {
    httpClient.get<HttpResponse>("/snapshots/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe emptyMap()
    }
  }

  test("put latest") {
    val next = mapOf("locations" to emptyList<Unit>())
    httpClient.put<HttpResponse>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = next
    }.apply {
      status shouldBe HttpStatusCode.NoContent
    }

    httpClient.get<HttpResponse>("/snapshots/latest").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe next
    }
  }
})