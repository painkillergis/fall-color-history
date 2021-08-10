package com.painkillergis.fall_color_history.history

import com.painkillergis.fall_color_history.util.BFunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class HistoryBTest : BFunSpec({ httpClient ->
  afterEach {
    httpClient.delete<Unit>("/latest")
    httpClient.delete<Unit>("/history")
  }

  test("no history") {
    httpClient.get<HttpResponse>("/history").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe buildJsonObject {
        put("history", buildJsonArray { })
      }
    }
  }

  test("history after updates") {
    httpClient.put<Unit>("/latest") {
      contentType(ContentType.Application.Json)
      body = buildJsonObject {
        put("the", "first update")
      }
    }

    httpClient.put<Unit>("/latest") {
      contentType(ContentType.Application.Json)
      body = buildJsonObject {
        put("the", "second update")
      }
    }

    httpClient.get<HttpResponse>("/history").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe buildJsonObject {
        putJsonArray("history") {
          addJsonObject {
            put("the", "first update")
          }
          addJsonObject {
            put("the", "second update")
          }
        }
      }
    }
  }

  test("discard duplicate updates") {
    httpClient.put<Unit>("/latest") {
      contentType(ContentType.Application.Json)
      body = buildJsonObject {
        put("the", "same update")
      }
    }

    httpClient.put<Unit>("/latest") {
      contentType(ContentType.Application.Json)
      body = buildJsonObject {
        put("the", "same update")
      }
    }

    httpClient.get<JsonObject>("/history") shouldBe
        buildJsonObject {
          putJsonArray("history") {
            addJsonObject {
              put("the", "same update")
            }
          }
        }
  }
})