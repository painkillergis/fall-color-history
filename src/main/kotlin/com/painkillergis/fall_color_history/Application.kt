package com.painkillergis.fall_color_history

import com.painkillergis.fall_color_history.latest.latestController
import com.painkillergis.fall_color_history.version.VersionService
import com.painkillergis.fall_color_history.version.versionController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Application.globalModules() {
  install(ContentNegotiation) {
    json()
  }
}

fun Application.controllers() {
  latestController()
  versionController(
    VersionService(),
  )
}

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
  globalModules()
  controllers()
}