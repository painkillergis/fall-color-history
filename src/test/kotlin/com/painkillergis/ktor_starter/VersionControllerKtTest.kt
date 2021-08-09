package com.painkillergis.ktor_starter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VersionControllerKtTest : FunSpec({
  test("get /version returns version number") {
    withTestApplication({
      globalModules()
      versionController()
    }) {
      handleRequest(HttpMethod.Get, "/version").apply {
        response.status() shouldBe HttpStatusCode.OK
        Json.decodeFromString<Map<String, String>>(response.content!!)["version"] shouldMatch Regex("\\d+")
      }
    }
  }
})