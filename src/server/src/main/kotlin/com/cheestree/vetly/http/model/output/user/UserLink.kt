package com.cheestree.vetly.http.model.output.user

import java.util.UUID

data class UserLink(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
)
