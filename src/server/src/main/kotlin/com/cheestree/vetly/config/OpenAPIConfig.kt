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
                    if (!responses.containsKey("400")) {
                        responses.addApiResponse("400", ApiResponse().`$ref`("#/components/responses/BadRequest"))
                    }
                    if (!responses.containsKey("403")) {
                        responses.addApiResponse("403", ApiResponse().`$ref`("#/components/responses/Forbidden"))
                    }
                    if (!responses.containsKey("404")) {
                        responses.addApiResponse("404", ApiResponse().`$ref`("#/components/responses/NotFound"))
                    }
                    if (!responses.containsKey("409")) {
                        responses.addApiResponse("409", ApiResponse().`$ref`("#/components/responses/Conflict"))
                    }
                    if (!responses.containsKey("401")) {
                        responses.addApiResponse("401", ApiResponse().`$ref`("#/components/responses/Unauthorized"))
                    }
                    if (!responses.containsKey("500")) {
                        responses.addApiResponse("500", ApiResponse().`$ref`("#/components/responses/InternalError"))
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
