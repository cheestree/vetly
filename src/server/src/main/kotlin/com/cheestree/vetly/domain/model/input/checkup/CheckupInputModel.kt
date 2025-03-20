package com.cheestree.vetly.domain.model.input.checkup

import java.time.OffsetDateTime

data class CheckupInputModel(
    val ownerId: Long,
    val petId: Long,
    val vetId: Long,
    val clinicId: Long,
    val time: OffsetDateTime,
    val description: String
)