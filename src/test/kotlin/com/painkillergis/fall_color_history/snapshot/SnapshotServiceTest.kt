package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SnapshotServiceTest : FunSpec({
  val timestampService = mockk<TimestampService>()
  val snapshotService = SnapshotService(Database(), timestampService)
  beforeEach {
    every { timestampService.getTimestamp() } returnsMany listOf("first", "second", "third")
  }

  afterEach {
    snapshotService.clear()
  }

  test("default latest") {
    snapshotService.getLatest() shouldBe SnapshotContainer()
  }

  test("replace default latest") {
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe SnapshotContainer("first", latest)
    snapshotService.getHistory() shouldBe listOf(SnapshotContainer("first", latest))
  }

  test("replace latest") {
    val oldest = mapOf("the" to "old oldest")
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(oldest)
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe SnapshotContainer("second", latest)
    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", oldest),
      SnapshotContainer("second", latest),
    )
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
      SnapshotContainer("first", mapOf("the" to "same update")),
    )
  }

  test("preserve non-sequential duplicate updates") {
    snapshotService.replaceLatest(mapOf("the" to "same update"))
    snapshotService.replaceLatest(mapOf("the" to "different update"))
    snapshotService.replaceLatest(mapOf("the" to "same update"))

    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", mapOf("the" to "same update")),
      SnapshotContainer("second", mapOf("the" to "different update")),
      SnapshotContainer("third", mapOf("the" to "same update")),
    )
  }
})
