package com.cheestree.vetly

import com.cheestree.vetly.converter.CustomOffsetDateTimeDeserializer
import com.cheestree.vetly.converter.CustomOffsetDateTimeSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

object TestUtils {
    private val baseDateTime: OffsetDateTime = OffsetDateTime.of(2025, 4, 14, 12, 0, 0, 0, ZoneOffset.UTC)

    val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .apply {
            val module = SimpleModule()
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            module.addSerializer(OffsetDateTime::class.java, CustomOffsetDateTimeSerializer(dateTimeFormatter))
            module.addDeserializer(OffsetDateTime::class.java, CustomOffsetDateTimeDeserializer(dateTimeFormatter))
            registerModule(module)
        }

    fun ResultActions.andExpectErrorResponse(
        expectedStatus: HttpStatus,
        expectedMessage: String,
        expectedError: String
    ): ResultActions {
        return this.andExpect(status().`is`(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(expectedMessage))
            .andExpect(jsonPath("$.error").value(expectedError))
    }

    inline fun <reified T> ResultActions.andExpectSuccessResponse(
        expectedStatus: HttpStatus,
        expectedMessage: String? = null,
        expectedData: T? = null
    ): ResultActions {
        if(expectedMessage == null) return this

        return this.andExpect(status().`is`(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo {
                val responseString = it.response.contentAsString
                val responseBody: T = mapper.readValue(responseString)

                assertEquals(expectedData, responseBody)
            }
    }

    fun daysAgo(days: Long = 0): OffsetDateTime =
        baseDateTime.minusDays(days).truncatedTo(ChronoUnit.MINUTES)

    fun Any.toJson(): String = mapper.writeValueAsString(this)
}