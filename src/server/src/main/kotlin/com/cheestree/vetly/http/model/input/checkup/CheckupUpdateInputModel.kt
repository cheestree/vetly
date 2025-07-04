package com.cheestree.vetly.http.model.input.checkup

import jakarta.annotation.Nullable
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class CheckupUpdateInputModel(
    @field:Size(max = 64, message = "Description must be at most 64 characters long")
    val title: String? = null,
    @field:Nullable
    @field:FutureOrPresent(message = "Checkup date must be in the present or future")
    val dateTime: OffsetDateTime? = null,
    @field:Size(max = 256, message = "Description must be at most 256 characters long")
    val description: String? = null,
)
