package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.enums.Role

data class UserInformation(
    val id: Long,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val roles: List<Role>
)