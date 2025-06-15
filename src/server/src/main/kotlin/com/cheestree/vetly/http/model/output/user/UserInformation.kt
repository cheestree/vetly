package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.user.roles.Role
import java.time.LocalDate
import java.util.UUID

data class UserInformation(
    val id: UUID,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val roles: Set<Role>,
    val joinedAt: LocalDate,
)
