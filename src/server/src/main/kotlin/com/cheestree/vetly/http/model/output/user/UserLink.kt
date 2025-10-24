package com.cheestree.vetly.http.model.output.user

import java.util.*

data class UserLink(
    val id: UUID,
    val name: String,
    val image: String?,
)
