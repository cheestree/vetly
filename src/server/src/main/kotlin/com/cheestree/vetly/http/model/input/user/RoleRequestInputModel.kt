package com.cheestree.vetly.http.model.input.user

data class RoleRequestInputModel(
    val requestedRole: String,
    val justification: String?,
    val fileUrl: String? = null,
)