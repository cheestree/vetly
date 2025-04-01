package com.cheestree.vetly.converter

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class CustomOffsetDateTimeSerializer : JsonSerializer<OffsetDateTime>() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")

    override fun serialize(value: OffsetDateTime?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value?.format(formatter))
    }
}