package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.toJsonElement
import com.painkillergis.fall_color_history.util.toJsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray

@Serializable
data class HistoryContainer(val history: List<SnapshotContainer>)

@Serializable
data class SnapshotContainer(
  val timestamp: String = "",
  val content: LocationsContainer = LocationsContainer(),
)

@Serializable
data class LocationsContainer(
  val locations: JsonArray = buildJsonArray { },
) {
  constructor(vararg locations: Map<String, Any>) : this(locations.toJsonElement() as JsonArray)
}