package com.cheestree.vetly.http.model.input.checkup

import java.time.OffsetDateTime

data class CheckupUpdateInputModel(
    val veterinarianId: Long? = null,
    val dateTime: OffsetDateTime? = null,
    val description: String? = null,
)