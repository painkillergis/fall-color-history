package com.painkillergis.fall_color_history.snapshot

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimestampService {
  fun getTimestamp(): String =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(Instant.now().atZone(ZoneId.systemDefault()))
}