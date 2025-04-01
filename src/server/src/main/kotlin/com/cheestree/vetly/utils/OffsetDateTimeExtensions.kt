package com.cheestree.vetly.utils

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

fun OffsetDateTime.truncateToMillis(): OffsetDateTime {
    return this.truncatedTo(ChronoUnit.MILLIS)
}