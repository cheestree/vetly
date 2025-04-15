package com.cheestree.vetly.unit.serializer

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.converter.CustomOffsetDateTimeDeserializer
import com.cheestree.vetly.converter.CustomOffsetDateTimeSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.Test

class CustomOffsetDateTimeSerializerTest {

    private val appConfig = AppConfig()

    private val formatter = DateTimeFormatter.ofPattern(appConfig.dateFormat)
    private val module = SimpleModule().apply {
        addSerializer(OffsetDateTime::class.java, CustomOffsetDateTimeSerializer(formatter))
        addDeserializer(OffsetDateTime::class.java, CustomOffsetDateTimeDeserializer(formatter))
    }

    private val mapper = ObjectMapper().registerModule(module)

    @Test
    fun `should serialize OffsetDateTime to custom format`() {
        val dateTime = OffsetDateTime.of(2025, 4, 14, 15, 0, 0, 0, ZoneOffset.UTC)
        val json = mapper.writeValueAsString(dateTime)
        val expected = "\"2025-04-14T15:00:00+0000\""
        println("Serialized JSON: $json")
        println("Expected JSON: $expected")
        assertEquals(expected, json)
    }

    @Test
    fun `should deserialize OffsetDateTime from custom format`() {
        val json = "\"2025-04-14T15:00:00+0000\""
        val parsed = mapper.readValue(json, OffsetDateTime::class.java)
        val expected = OffsetDateTime.of(2025, 4, 14, 15, 0, 0, 0, ZoneOffset.UTC)
        println("Parsed JSON: $json")
        println("Expected parsed: $expected")
        assertEquals(expected, parsed)
    }

    @Test
    fun `should fail deserialization for invalid format`() {
        val invalidJson = "\"14/04/2025 15:00:00 +0000\""

        val exception = assertThrows<Exception> {
            mapper.readValue(invalidJson, OffsetDateTime::class.java)
        }

        println("Expected failure: ${exception.message}")
    }
}