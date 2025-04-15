package com.cheestree.vetly.converter

import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

class Parsers {
    companion object {
        fun parseOffsetDateTime(dateString: String?): OffsetDateTime? {
            return try {
                dateString?.let { OffsetDateTime.parse(it) }
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }
}