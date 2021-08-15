package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.Database
import com.painkillergis.fall_color_history.util.toJsonElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SnapshotServiceTest : FunSpec({
  val snapshotService = SnapshotService(Database())

  afterEach {
    snapshotService.clear()
  }

  test("default latest") {
    snapshotService.getLatest() shouldBe emptyMap()
  }

  test("replace default latest") {
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe latest.toJsonElement()
    snapshotService.getHistory() shouldBe listOf(latest.toJsonElement())
  }

  test("replace latest") {
    val oldest = mapOf("the" to "old oldest")
    val latest = mapOf("the" to "late latest")
    snapshotService.replaceLatest(oldest)
    snapshotService.replaceLatest(latest)

    snapshotService.getLatest() shouldBe latest.toJsonElement()
    snapshotService.getHistory() shouldBe listOf(
      oldest,
      latest,
    ).toJsonElement()
  }

  test("clear") {
    snapshotService.replaceLatest(mapOf("the" to "update to clear"))
    snapshotService.clear()

    snapshotService.getLatest() shouldBe emptyMap()
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
