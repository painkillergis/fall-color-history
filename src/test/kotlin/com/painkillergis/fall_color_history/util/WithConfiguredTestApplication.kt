package com.painkillergis.fall_color_history.util

import com.painkillergis.fall_color_history.controllers
import com.painkillergis.fall_color_history.globalModules
import io.ktor.server.testing.*

fun withConfiguredTestApplication(block: TestApplicationEngine.() -> Unit) =
  withTestApplication({
    globalModules()
    controllers()
  }, block)