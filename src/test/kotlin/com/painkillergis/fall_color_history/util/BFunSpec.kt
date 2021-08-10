package com.painkillergis.fall_color_history.util

import io.kotest.core.spec.style.FunSpec

abstract class BFunSpec(val body: FunSpec.() -> Unit) : FunSpec({
  listeners(EmbeddedServerTestListener.ServerStart)
  body(this)
})