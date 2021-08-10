package com.painkillergis.fall_color_history.version

import com.painkillergis.fall_color_history.util.EmbeddedServerTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class VersionBTest : FunSpec({
  listeners(EmbeddedServerTestListener)

  test("get /version returns version") {
    EmbeddedServerTestListener.withEmbeddedServerHttpClient {
      get<HttpResponse>("/version").apply {
        status shouldBe HttpStatusCode.OK
        receive<Map<String, String>>()["version"] shouldMatch Regex("\\d+")
      }
    }
  }
})