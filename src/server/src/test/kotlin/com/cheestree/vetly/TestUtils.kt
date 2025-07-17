package com.cheestree.vetly

import com.cheestree.vetly.config.JacksonConfig
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

object TestUtils {
    private val baseDateTime: OffsetDateTime =
        LocalDate
            .now(ZoneOffset.UTC)
            .atTime(12, 0)
            .atOffset(ZoneOffset.UTC)

    val mapper = JacksonConfig().objectMapper()

    fun ResultActions.andExpectErrorResponse(
        expectedStatus: HttpStatus,
        expectedMessage: String,
        expectedErrorDetails: List<Pair<String?, String>>,
    ): ResultActions {
        //  val content = this.andReturn().response.contentAsString
        //  println("RESPONSE CONTENT: $content")

        val result =
            this
                .andExpect(status().`is`(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.details.length()").value(expectedErrorDetails.size))

        expectedErrorDetails.forEachIndexed { index, (field, error) ->
            if (field != null) {
                result.andExpect(jsonPath("$.details[$index].field").value(field))
            } else {
                result.andExpect(jsonPath("$.details[$index].field").isEmpty)
            }
            result.andExpect(jsonPath("$.details[$index].error").value(error))
        }

        return result
    }

    inline fun <reified T> ResultActions.andExpectSuccessResponse(
        expectedStatus: HttpStatus,
        expectedMessage: String? = null,
        expectedData: T? = null,
    ): ResultActions {
        if (expectedMessage == null) return this

        return this
            .andExpect(status().`is`(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo {
                val responseString = it.response.contentAsString
                val responseBody: T = mapper.readValue(responseString)

                assertEquals(expectedData, responseBody)
            }
    }

    fun daysFromNow(days: Long = 1): OffsetDateTime = baseDateTime.plusDays(days).truncatedTo(ChronoUnit.MINUTES)

    fun daysAgo(days: Long = 1): OffsetDateTime = baseDateTime.minusDays(days).truncatedTo(ChronoUnit.MINUTES)

    fun Any.toJson(): String = mapper.writeValueAsString(this)
}
