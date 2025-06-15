package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.serializer.RequestCreateInputModelDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank

@JsonDeserialize(using = RequestCreateInputModelDeserializer::class)
data class RequestCreateInputModel(
    val action: RequestAction,
    val target: RequestTarget,
    @field:NotBlank
    val justification: String,
    val extraData: RequestExtraData?,
    val files: List<String>,
)
