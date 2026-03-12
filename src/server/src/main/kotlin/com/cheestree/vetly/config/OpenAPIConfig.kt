package com.cheestree.vetly.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
class OpenAPIConfig {
    private companion object {
        private val DEFAULT_ERROR_RESPONSES =
            mapOf(
                "400" to "BadRequest",
                "401" to "Unauthorized",
                "403" to "Forbidden",
                "404" to "NotFound",
                "409" to "Conflict",
                "500" to "InternalError",
            )
    }

    @Bean
    @Order(1)
    fun globalResponses(): OpenApiCustomiser =
        OpenApiCustomiser { openApi ->
            val components = openApi.components ?: Components().also { openApi.components = it }

            components.addResponses("BadRequest", buildApiResponse("Bad request"))
            components.addResponses("Forbidden", buildApiResponse("Forbidden"))
            components.addResponses("NotFound", buildApiResponse("Not found"))
            components.addResponses("Conflict", buildApiResponse("Conflict"))

            components.addSchemas("ApiError", Schema<Any>().`$ref`("#/components/schemas/ApiError"))
        }

    @Bean
    @Order(2)
    fun injectDefaultErrorResponses(): OpenApiCustomiser =
        OpenApiCustomiser { openApi ->
            openApi.paths?.values?.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    val responses = operation.responses
                    DEFAULT_ERROR_RESPONSES.forEach { (code, name) ->
                        responses.putIfAbsent(code, ApiResponse().`$ref`("#/components/responses/$name"))
                    }
                }
            }
        }

    private fun buildApiResponse(description: String): ApiResponse =
        ApiResponse().description(description).content(
            Content().addMediaType(
                "application/json",
                MediaType().schema(Schema<Any>().`$ref`("#/components/schemas/ApiError")),
            ),
        )
}
