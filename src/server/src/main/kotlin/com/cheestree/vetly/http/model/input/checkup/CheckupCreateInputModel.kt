package com.cheestree.vetly.http.model.input.checkup

import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.OffsetDateTime

data class CheckupCreateInputModel(
    @field:Min(value = 1, message = "Animal ID must be greater than 0")
    val animalId: Long,
    @field:Min(value = 1, message = "Veterinarian ID must be greater than 0")
    val veterinarianId: Long,
    @field:Min(value = 1, message = "Clinic ID must be greater than 0")
    val clinicId: Long,
    val dateTime: OffsetDateTime,
    @field:Max(value = 256, message = "Description must be at most 256 characters long")
    val description: String,
    val files: List<StoredFileInputModel>,
) : RequestExtraData
