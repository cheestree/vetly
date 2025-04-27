package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.user.roles.Role
import java.util.*

data class UserInformation(
    val publicId: UUID,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val roles: Set<Role>
)