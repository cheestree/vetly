package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.clinic.ClinicMembershipPreview
import java.time.LocalDate
import java.util.*

data class UserInformation(
    val id: UUID,
    val name: String,
    val email: String,
    val image: String?,
    val roles: Set<Role>,
    val clinics: List<ClinicMembershipPreview>,
    val joinedAt: LocalDate,
)
