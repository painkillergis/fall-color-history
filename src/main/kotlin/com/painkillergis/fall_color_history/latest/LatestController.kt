package com.painkillergis.fall_color_history.latest

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.latestController() =
  routing {
    get("/latest") {
      call.respond(mapOf("the" to "latest"))
    }
  }