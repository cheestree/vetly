package com.cheestree.vetly.http.model.output.checkup

import com.cheestree.vetly.domain.checkup.status.CheckupStatus
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicLink
import com.cheestree.vetly.http.model.output.file.FileInformation
import com.cheestree.vetly.http.model.output.user.UserLink
import java.time.OffsetDateTime

data class CheckupInformation(
    val id: Long,
    val title: String,
    val description: String,
    val dateTime: OffsetDateTime,
    val status: CheckupStatus,
    val animal: AnimalInformation,
    val veterinarian: UserLink,
    val clinic: ClinicLink,
    val files: List<FileInformation>,
)
