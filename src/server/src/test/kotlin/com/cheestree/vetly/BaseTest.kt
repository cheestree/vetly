package com.cheestree.vetly

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.assertEquals

open class BaseTest {
    companion object {
        val mapper: ObjectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

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

        fun Any.toJson(): String = mapper.writeValueAsString(this)
    }
}