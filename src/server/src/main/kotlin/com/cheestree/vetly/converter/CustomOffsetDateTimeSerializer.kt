package com.cheestree.vetly.converter

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CustomOffsetDateTimeSerializer(
    private val formatter: DateTimeFormatter,
) : JsonSerializer<OffsetDateTime>() {
    override fun serialize(
        value: OffsetDateTime,
        gen: JsonGenerator,
        provider: SerializerProvider,
    ) {
        val standardizedValue =
            value
                .truncatedTo(ChronoUnit.MINUTES)
                .withOffsetSameInstant(ZoneOffset.UTC)
        gen.writeString(standardizedValue.format(formatter))
    }
}
