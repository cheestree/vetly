package com.cheestree.vetly.http.model.output.user

import java.util.UUID

data class UserPreview(
    val id: UUID,
    val name: String,
    val email: String,
    val imageUrl: String?,
)
