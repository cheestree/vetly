package com.cheestree.vetly.http.model.output.user

import com.cheestree.vetly.domain.request.type.RequestStatus
import java.time.OffsetDateTime
import java.util.UUID

data class RoleRequestInformation(
    val id: UUID,
    val requestedRole: String,
    val status: RequestStatus,
    val justification: String?,
    val fileUrl: String?,
    val submittedAt: OffsetDateTime,
)
