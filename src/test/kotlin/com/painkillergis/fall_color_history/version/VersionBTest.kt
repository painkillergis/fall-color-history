package com.painkillergis.fall_color_history.version

import com.painkillergis.fall_color_history.util.BFunSpec
import com.painkillergis.fall_color_history.util.EmbeddedServerTestListener.withEmbeddedServerHttpClient
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class VersionBTest : BFunSpec({
  test("get /version returns version") {
    withEmbeddedServerHttpClient {
      get<HttpResponse>("/version").apply {
        status shouldBe HttpStatusCode.OK
        receive<Map<String, String>>()["version"] shouldMatch Regex("\\d+")
      }
    }
  }
})