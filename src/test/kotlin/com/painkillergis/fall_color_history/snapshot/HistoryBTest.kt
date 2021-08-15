package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.BFunSpec
import com.painkillergis.fall_color_history.util.toJsonElement
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class HistoryBTest : BFunSpec({ httpClient ->
  afterEach {
    httpClient.delete<Unit>("/snapshots")
    httpClient.delete<Unit>("/snapshots/latest")
  }

  test("no history") {
    httpClient.get<HttpResponse>("/snapshots").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe mapOf("history" to emptyList<Unit>())
    }
  }

  test("history after updates") {
    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = mapOf("the" to "first update")
    }

    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = mapOf("the" to "second update")
    }

    httpClient.get<HttpResponse>("/snapshots").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe mapOf(
        "history" to listOf(
          mapOf("the" to "first update"),
          mapOf("the" to "second update"),
        )
      ).toJsonElement()
    }
  }

  test("discard duplicate updates") {
    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = mapOf("the" to "same update")
    }

    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = mapOf("the" to "same update")
    }

    httpClient.get<JsonObject>("/snapshots") shouldBe
      mapOf(
        "history" to listOf(
          mapOf("the" to "same update")
        )
      ).toJsonElement()
  }
})