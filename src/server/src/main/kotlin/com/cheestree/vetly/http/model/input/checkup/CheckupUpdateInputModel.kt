package com.cheestree.vetly.http.model.input.checkup

import java.time.OffsetDateTime

data class CheckupUpdateInputModel(
    val clinicId: Long,
    val checkupId: Long,
    val vetId: Long? = null,
    val time: OffsetDateTime? = null,
    val description: String? = null,
)