package com.cheestree.vetly.http.model.input.checkup

import java.time.OffsetDateTime

data class CheckupCreateInputModel(
    val ownerId: Long,
    val petId: Long,
    val vetId: Long,
    val clinicId: Long,
    val time: OffsetDateTime,
    val description: String
)