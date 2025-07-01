package com.cheestree.vetly.http.model.input.checkup

import com.cheestree.vetly.http.model.input.request.RequestExtraData
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

data class CheckupCreateInputModel(
    @field:Min(value = 1, message = "Animal ID must be greater than 0")
    val animalId: Long,
    val veterinarianId: UUID? = null,
    @field:Min(value = 1, message = "Clinic ID must be greater than 0")
    val clinicId: Long,
    val dateTime: OffsetDateTime,
    @field:Size(max = 64, message = "Title must be at most 64 characters long")
    val title: String,
    @field:Size(max = 256, message = "Description must be at most 256 characters long")
    val description: String,
) : RequestExtraData
