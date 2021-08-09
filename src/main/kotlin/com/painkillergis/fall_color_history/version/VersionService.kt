package com.painkillergis.fall_color_history.version

class VersionService {
  fun get() = javaClass.classLoader.getResource("version")!!.readText()
}
