package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestStatus
import jakarta.validation.constraints.NotBlank

data class RequestUpdateInputModel(
    val decision: RequestStatus,
    @field:NotBlank(message = "Justification cannot be blank")
    val justification: String,
)
