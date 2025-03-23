package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.enums.Role

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val roles: List<Role>,
)