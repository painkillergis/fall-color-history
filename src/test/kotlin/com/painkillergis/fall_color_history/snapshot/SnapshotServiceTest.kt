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
    snapshotService.getLatestSnapshot() shouldBe SnapshotContainer()
  }

  test("replace default latest") {
    val latest = LocationsContainer(mapOf("the" to "late latest"))
    snapshotService.replaceLatest(latest)

    snapshotService.getLatestSnapshot() shouldBe SnapshotContainer("first", latest)
    snapshotService.getHistory() shouldBe listOf(SnapshotContainer("first", latest))
  }

  test("replace latest") {
    val oldest = LocationsContainer(mapOf("the" to "old oldest"))
    val latest = LocationsContainer(mapOf("the" to "late latest"))
    snapshotService.replaceLatest(oldest)
    snapshotService.replaceLatest(latest)

    snapshotService.getLatestSnapshot() shouldBe SnapshotContainer("second", latest)
    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", oldest),
      SnapshotContainer("second", latest),
    )
  }

  test("clear") {
    snapshotService.replaceLatest(LocationsContainer(mapOf("the" to "update to clear")))
    snapshotService.clear()

    snapshotService.getLatestSnapshot() shouldBe SnapshotContainer()
    snapshotService.getHistory() shouldBe emptyList<Unit>()
  }

  test("discard duplicate updates") {
    val update = LocationsContainer(mapOf("the" to "same update"))
    snapshotService.replaceLatest(update)
    snapshotService.replaceLatest(update)

    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", update),
    )
  }

  test("preserve non-sequential duplicate updates") {
    val same = LocationsContainer(mapOf("the" to "same update"))
    val different = LocationsContainer(mapOf("the" to "different update"))
    snapshotService.replaceLatest(same)
    snapshotService.replaceLatest(different)
    snapshotService.replaceLatest(same)

    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", same),
      SnapshotContainer("second", different),
      SnapshotContainer("third", same),
    )
  }

  test("photo field does not impact distinctness of a snapshot") {
    val withPhoto = LocationsContainer(mapOf("the" to "same update", "photo" to "photo"))
    val withoutPhoto = LocationsContainer(mapOf("the" to "same update"))
    snapshotService.replaceLatest(withPhoto)
    snapshotService.replaceLatest(withoutPhoto)

    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("first", withPhoto),
    )

    snapshotService.clear()

    snapshotService.replaceLatest(withoutPhoto)
    snapshotService.replaceLatest(withPhoto)

    snapshotService.getHistory() shouldBe listOf(
      SnapshotContainer("second", withoutPhoto),
    )
  }
})
