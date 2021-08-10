package com.painkillergis.fall_color_history.util

import com.painkillergis.fall_color_history.module
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.test.TestCase
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

object EmbeddedServerTestListener {
  private var started = false

  private val server = embeddedServer(Netty, port = 8080) { module() }

  private val httpClient = HttpClient {
    defaultRequest {
      url.protocol = URLProtocol.HTTP
      url.host = "localhost"
      url.port = 8080
    }
    install(JsonFeature)
  }

  object ServerStart : io.kotest.core.listeners.TestListener {
    override suspend fun beforeTest(testCase: TestCase) {
      if (started) return
      server.start()
      started = true
      withTimeout(4000) {
        while (!isRunning()) {
          delay(250)
        }
      }
    }

    private suspend fun isRunning() =
      try {
        httpClient.get<HttpResponse>("/version").status == HttpStatusCode.OK
      } catch (exception: Exception) {
        false
      }
  }

  @AutoScan
  object ServerStop : ProjectListener {
    override suspend fun afterProject() {
      server.stop(1000, 1000)
    }
  }

  suspend fun withEmbeddedServerHttpClient(block: suspend HttpClient.() -> Unit) =
    block(httpClient)
}
