package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.http.model.output.user.UserLink
import java.time.LocalDate

data class ClinicMembershipPreview(
    val user: UserLink,
    val clinic: ClinicLink,
    val joinedAt: LocalDate,
    val leftIn: LocalDate?,
)