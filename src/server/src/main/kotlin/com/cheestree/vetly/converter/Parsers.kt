package com.cheestree.vetly.converter

import org.springframework.data.domain.Sort
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

        fun parseSortDirection(direction: String?): Sort.Direction {
            return try {
                Sort.Direction.valueOf(direction?.uppercase() ?: "DESC")
            } catch (e: IllegalArgumentException) {
                Sort.Direction.DESC
            }
        }
    }
}