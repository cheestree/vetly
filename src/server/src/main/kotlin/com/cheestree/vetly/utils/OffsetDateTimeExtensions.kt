package com.cheestree.vetly.utils

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

fun OffsetDateTime.truncateToMillis(): OffsetDateTime = this.truncatedTo(ChronoUnit.MILLIS)
