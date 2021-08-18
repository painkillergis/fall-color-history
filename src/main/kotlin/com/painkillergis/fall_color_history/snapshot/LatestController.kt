package com.painkillergis.fall_color_history.snapshot

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.JsonObject
import org.slf4j.Logger

fun Application.latestController(
  snapshotService: SnapshotService,
  log: Logger,
) =
  routing {
    get("/snapshots/latest") {
      try {
        call.respond(snapshotService.getLatestSnapshot())
      } catch (exception: Exception) {
        log.error("There was an error getting the latest", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
    put("/snapshots/latest") {
      try {
        snapshotService.replaceLatest(call.receive())
        call.respond(HttpStatusCode.NoContent)
      } catch (exception: Exception) {
        log.error("There was an error setting the latest", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
  }