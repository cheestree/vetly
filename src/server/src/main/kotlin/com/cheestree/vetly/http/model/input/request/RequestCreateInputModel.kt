package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.serializer.RequestCreateInputModelDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@JsonDeserialize(using = RequestCreateInputModelDeserializer::class)
data class RequestCreateInputModel(
    val action: RequestAction,
    val target: RequestTarget,
    @field:NotBlank
    @field:Size(max = 256, message = "Justification must be at most 256 characters long")
    val justification: String,
    val extraData: RequestExtraData? = null,
)
