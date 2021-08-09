package com.painkillergis.fall_color_history.version

import com.painkillergis.fall_color_history.globalModules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VersionControllerKtTest : FunSpec({
  val versionService = mockk<VersionService>()

  test("get /version returns version number") {
    withTestApplication({
      globalModules()
      versionController(
        versionService,
      )
    }) {
      every {versionService.get()} returns "the version"

      handleRequest(HttpMethod.Get, "/version").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!) shouldBe mapOf("version" to "the version")
      }
    }
  }
})