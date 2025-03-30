package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.user.roles.Status
import java.time.OffsetDateTime
import java.util.*

data class RoleRequestInformation(
    val id: UUID,
    val requestedRole: String,
    val status: Status,
    val justification: String?,
    val fileUrl: String?,
    val submittedAt: OffsetDateTime,
)