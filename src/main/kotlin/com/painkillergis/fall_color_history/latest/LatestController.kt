package com.painkillergis.fall_color_history.latest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.JsonObject
import org.slf4j.Logger

fun Application.latestController(
  latestService: LatestService,
  log: Logger,
) =
  routing {
    get("/latest") {
      try {
        call.respond(latestService.get())
      } catch (exception: Exception) {
        log.error("There was an error getting the latest", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
    put("/latest") {
      try {
        latestService.put(call.receive<JsonObject>())
        call.respond(HttpStatusCode.NoContent)
      } catch (exception: Exception) {
        log.error("There was an error setting the latest", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
    delete("/latest") {
      try {
        latestService.clear()
        call.respond(HttpStatusCode.NoContent)
      } catch (exception: Exception) {
        log.error("There was an error clearing the latest", exception)
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
  }