package com.cheestree.vetly.http.model.input.checkup

import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import jakarta.annotation.Nullable
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class CheckupUpdateInputModel(
    @field:Max(value = 64, message = "Description must be at most 64 characters long")
    val title: String? = null,
    @field:Min(value = 1, message = "Veterinarian ID must be greater than 0")
    val veterinarianId: Long? = null,
    @field:Nullable
    @field:FutureOrPresent(message = "Checkup date must be in the present or future")
    val dateTime: OffsetDateTime? = null,
    @field:Size(max = 256, message = "Description must be at most 256 characters long")
    val description: String? = null,
    val filesToAdd: List<
        @Min(1)
        StoredFileInputModel,
    >? = null,
    val filesToRemove: List<
        @Min(1)
        Long,
    >? = null,
)
