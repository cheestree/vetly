package com.cheestree.vetly.http.model.input.checkup

import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import java.time.OffsetDateTime

data class CheckupCreateInputModel(
    val animalId: Long,
    val veterinarianId: Long,
    val clinicId: Long,
    val dateTime: OffsetDateTime,
    val description: String,
    val files: List<StoredFileInputModel>
)