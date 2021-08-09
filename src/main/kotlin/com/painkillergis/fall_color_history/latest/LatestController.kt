package com.painkillergis.fall_color_history.latest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.latestController(latestService: LatestService) =
  routing {
    get("/latest") {
      try {
        call.respond(latestService.get())
      } catch (exception: Exception) {
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
    put("/latest") {
      try {
        latestService.put(call.receive())
        call.respond(HttpStatusCode.NoContent)
      } catch (exception: Exception) {
        call.respond(HttpStatusCode.InternalServerError)
      }
    }
  }