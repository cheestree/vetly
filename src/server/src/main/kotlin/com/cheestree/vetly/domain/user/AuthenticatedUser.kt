package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.user.roles.Role
import java.util.UUID

data class AuthenticatedUser(
    val id: Long,
    val uid: String? = null,
    val publicId: UUID,
    val name: String,
    val email: String,
    val roles: Set<Role>,
)
