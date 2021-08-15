package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.toJsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class SnapshotContainer(
  val timestamp: String = "",
  val content: JsonObject = buildJsonObject { },
) {
  constructor(timestamp: String, content: Map<String, Any>) : this(
    timestamp,
    content.toJsonObject(),
  )
}
