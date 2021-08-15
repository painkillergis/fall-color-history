package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import com.painkillergis.fall_color_history.util.toJsonObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SnapshotServiceTest : FunSpec({
  val timestampService = mockk<TimestampService>() {
    every { getTimestamp() } returns "the timestamp"
  }
  val snapshotService = SnapshotService(Database(), timestampService)

  afterEach {
    snapshotService.clear()
  }

  test("default latest") {
    snapshotService.getLatest() shouldBe SnapshotContainer()
  }

  test("replace default latest") {
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe SnapshotContainer("the timestamp", latest)
    snapshotService.getHistory() shouldBe listOf(latest.toJsonElement())
  }

  test("replace latest") {
    val oldest = mapOf("the" to "old oldest")
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(oldest)
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe SnapshotContainer("the timestamp", latest)
    snapshotService.getHistory() shouldBe listOf(
      oldest,
      latest,
    ).toJsonElement()
  }

  test("clear") {
    snapshotService.replaceLatest(mapOf("the" to "update to clear"))
    snapshotService.clear()

    snapshotService.getLatest() shouldBe SnapshotContainer()
    snapshotService.getHistory() shouldBe emptyList<Unit>()
  }

  test("discard duplicate updates") {
    snapshotService.replaceLatest(mapOf("the" to "same update"))
    snapshotService.replaceLatest(mapOf("the" to "same update"))

    snapshotService.getHistory() shouldBe listOf(
      mapOf("the" to "same update"),
    ).toJsonElement()
  }

  test("preserve non-sequential duplicate updates") {
    snapshotService.replaceLatest(mapOf("the" to "same update"))
    snapshotService.replaceLatest(mapOf("the" to "different update"))
    snapshotService.replaceLatest(mapOf("the" to "same update"))

    snapshotService.getHistory() shouldBe listOf(
      mapOf("the" to "same update"),
      mapOf("the" to "different update"),
      mapOf("the" to "same update"),
    ).toJsonElement()
  }
})
