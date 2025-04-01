package com.cheestree.vetly.converter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class CustomOffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime>() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OffsetDateTime {
        return OffsetDateTime.parse(p.text, formatter)
    }
}