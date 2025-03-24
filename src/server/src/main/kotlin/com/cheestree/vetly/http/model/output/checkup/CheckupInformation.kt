package com.cheestree.vetly.http.model.output.checkup

import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.model.output.user.UserPreview
import java.time.OffsetDateTime

data class CheckupInformation(
    val id: Long,
    val description: String,
    val dateTime: OffsetDateTime,
    val missed: Boolean,
    val animal: AnimalInformation,
    val veterinarian: UserPreview,
    val clinic: ClinicPreview
)