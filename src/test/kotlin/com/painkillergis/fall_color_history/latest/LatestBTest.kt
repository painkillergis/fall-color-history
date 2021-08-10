package com.painkillergis.fall_color_history.latest

import com.painkillergis.fall_color_history.util.EmbeddedServerTestListener
import com.painkillergis.fall_color_history.util.EmbeddedServerTestListener.withEmbeddedServerHttpClient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LatestBTest : FunSpec({
  listeners(EmbeddedServerTestListener)

  test("no latest") {
    withEmbeddedServerHttpClient {
      get<HttpResponse>("/latest").apply {
        status shouldBe HttpStatusCode.OK
        receive<JsonObject>() shouldBe emptyMap()
      }
    }
  }

  test("put latest") {
    withEmbeddedServerHttpClient {
      put<HttpResponse>("/latest") {
        contentType(ContentType.Application.Json)
        body = mapOf("the" to "late latest")
      }.apply {
        status shouldBe HttpStatusCode.NoContent
      }

      get<HttpResponse>("/latest").apply {
        status shouldBe HttpStatusCode.OK
        receive<JsonObject>() shouldBe buildJsonObject { put("the", "late latest") }
      }
    }
  }
})