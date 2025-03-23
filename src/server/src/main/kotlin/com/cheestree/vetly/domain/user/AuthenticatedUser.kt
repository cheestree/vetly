package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.enums.Role

data class AuthenticatedUser(
    val id: Long,
    val uid: String? = null,
    val name: String,
    val email: String,
    val roles: List<Role>,
)