package com.cheestree.vetly.http.model.input.checkup

import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import java.time.OffsetDateTime

data class CheckupCreateInputModel(
    val petId: Long,
    val vetId: Long,
    val clinicId: Long,
    val time: OffsetDateTime,
    val description: String,
    val files: List<StoredFileInputModel>
)