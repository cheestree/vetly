package com.cheestree.vetly.http.model.output.request

import com.cheestree.vetly.http.model.output.user.UserPreview
import java.time.OffsetDateTime
import java.util.UUID

data class RequestPreview(
    val id: UUID,
    val user: UserPreview,
    val target: String,
    val action: String,
    val status: String,
    val justification: String?,
    val createdAt: OffsetDateTime,
)
