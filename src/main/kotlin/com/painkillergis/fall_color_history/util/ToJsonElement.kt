package com.painkillergis.fall_color_history.util

import kotlinx.serialization.json.*

fun Any?.toJsonElement(): JsonElement =
  when (this) {
    is JsonElement -> this
    is String -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Map<*, *> -> toJsonObject()
    is List<*> -> JsonArray(map { it?.toJsonElement() ?: JsonNull })
    is Array<*> -> JsonArray(map { it?.toJsonElement() ?: JsonNull })
    null -> JsonNull
    else -> throw Exception("Cannot convert unknown type $javaClass to JsonElement")
  }

fun Map<*, *>.toJsonObject(): JsonObject =
  JsonObject(
    mapKeys { it.key as String }
      .mapValues { it.value.toJsonElement() }
  )
