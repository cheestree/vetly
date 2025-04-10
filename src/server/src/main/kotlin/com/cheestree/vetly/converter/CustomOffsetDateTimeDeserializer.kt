package com.cheestree.vetly.converter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CustomOffsetDateTimeDeserializer(private val formatter: DateTimeFormatter) : JsonDeserializer<OffsetDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OffsetDateTime {
        val value = p.text
        return OffsetDateTime.parse(value, formatter)
            .truncatedTo(ChronoUnit.MINUTES)
            .withOffsetSameInstant(ZoneOffset.UTC)
    }
}