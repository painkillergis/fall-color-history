package com.painkillergis.fall_color_history.snapshot

import com.painkillergis.fall_color_history.util.BFunSpec
import com.painkillergis.fall_color_history.util.toJsonObject
import io.kotest.matchers.date.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import java.time.Instant
import java.time.format.DateTimeFormatter

class HistoryBTest : BFunSpec({ httpClient ->
  afterEach {
    httpClient.delete<Unit>("/snapshots")
  }

  test("no history") {
    httpClient.get<HttpResponse>("/snapshots").apply {
      status shouldBe HttpStatusCode.OK
      receive<JsonObject>() shouldBe mapOf("history" to emptyList<Unit>())
    }
  }

  test("history after updates") {
    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "first update"))
    }

    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "second update"))
    }

    httpClient.get<HttpResponse>("/snapshots").apply {
      status shouldBe HttpStatusCode.OK
      receive<HistoryContainer>().shouldBeHistory(
        SnapshotContainerMatcher(LocationsContainer(mapOf("the" to "first update"))),
        SnapshotContainerMatcher(LocationsContainer(mapOf("the" to "second update"))),
      )
    }
  }

  test("discard duplicate updates") {
    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "same update"))
    }

    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "same update"))
    }

    httpClient.get<HistoryContainer>("/snapshots").shouldBeHistory(
      SnapshotContainerMatcher(LocationsContainer(mapOf("the" to "same update"))),
    )
  }

  test("photo field does not impact distinctness of a snapshot") {
    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "same update", "photo" to "photo"))
    }

    httpClient.put<Unit>("/snapshots/latest") {
      contentType(ContentType.Application.Json)
      body = LocationsContainer(mapOf("the" to "same update"))
    }

    httpClient.get<HistoryContainer>("/snapshots").shouldBeHistory(
      SnapshotContainerMatcher(LocationsContainer(mapOf("the" to "same update", "photo" to "photo"))),
    )
  }
})

fun HistoryContainer.shouldBeHistory(vararg matcher: SnapshotContainerMatcher) {
  history.size shouldBe matcher.size
  history.zip(matcher).forEachIndexed { index, (actual, matcher) ->
    try {
      actual.content shouldBe matcher.content
      Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(actual.timestamp))
        .shouldBeBetween(
          matcher.timestamp.minusMillis(matcher.timestampToleranceMs),
          matcher.timestamp.plusMillis(matcher.timestampToleranceMs),
        )
    } catch (exception: AssertionError) {
      throw AssertionError("Snapshot containers did not match at index $index", exception)
    }
  }
}

data class SnapshotContainerMatcher(
  val content: LocationsContainer,
  val timestamp: Instant = Instant.now(),
  val timestampToleranceMs: Long = 1000,
)
