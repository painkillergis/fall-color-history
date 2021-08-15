package com.painkillergis.fall_color_history

import com.painkillergis.fall_color_history.snapshot.*
import com.painkillergis.fall_color_history.version.VersionService
import com.painkillergis.fall_color_history.version.versionController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.netty.*

fun Application.globalModules() {
  install(ContentNegotiation) {
    json()
  }
}

fun Application.controllers() {
  val snapshotService = SnapshotService(
    Database(),
  )
  historyController(
    snapshotService,
    log,
  )
  latestController(
    snapshotService,
    log,
  )
  versionController(
    VersionService(),
  )
}

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
  globalModules()
  controllers()
}