package com.painkillergis.fall_color_history.history

import com.painkillergis.fall_color_history.util.BFunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class HistoryBTest : BFunSpec({ httpClient ->
  test("no history") {
    httpClient.get<HttpResponse>("/history").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe buildJsonObject {
        put("history", buildJsonArray { })
      }
    }
  }
})