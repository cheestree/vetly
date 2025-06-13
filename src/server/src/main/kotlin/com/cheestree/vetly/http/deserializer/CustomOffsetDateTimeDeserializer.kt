package com.cheestree.vetly.http.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class CustomOffsetDateTimeDeserializer(
    private val formatter: DateTimeFormatter,
) : JsonDeserializer<OffsetDateTime>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): OffsetDateTime {
        val value = p.valueAsString
        return OffsetDateTime.parse(value, formatter)
    }
}
