package com.cheestree.vetly.domain.user

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val role: String,
)