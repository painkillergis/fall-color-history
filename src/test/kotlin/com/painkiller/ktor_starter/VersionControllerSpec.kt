package com.painkiller.ktor_starter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class VersionControllerSpec {

  @MockK
  lateinit var versionService: VersionService

  @InjectMockKs
  lateinit var versionController : VersionController

  @Test
  fun `get version`() {
    withTestApplication(moduleFunction = {
      versionController.apply { module() }
      globalModules()
    }) {
      val givenVersion = Version("the sha", "the version")
      every { versionService.getVersion() } returns givenVersion
      val call = handleRequest(method = HttpMethod.Get, uri = "/version")

      assertEquals(HttpStatusCode.OK, call.response.status())

      val version: Version = jacksonObjectMapper().readValue(call.response.byteContent!!)
      assertEquals(givenVersion, version)
    }
  }
}