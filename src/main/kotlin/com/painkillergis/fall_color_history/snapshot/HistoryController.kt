package com.painkillergis.fall_color_history.snapshot

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.Logger

fun Application.historyController(
  historyService: HistoryService,
  log: Logger,
) {
  routing {
    get("/snapshots") {
      try {
        call.respond(mapOf("history" to historyService.get()))
      } catch (exception: Exception) {
        log.error("There was an error getting history", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
    delete("/snapshots") {
      try {
        historyService.clear()
        call.respond(HttpStatusCode.NoContent)
      } catch (exception: Exception) {
        log.error("There was an error clearing history", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
  }
}