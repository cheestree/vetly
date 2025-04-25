package com.cheestree.vetly.http.model.output.request

import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.http.model.output.user.UserInformation
import java.time.OffsetDateTime
import java.util.*

data class RequestInformation(
    val id: UUID,
    val user: UserInformation,
    val target: String,
    val action: String,
    val requestStatus: RequestStatus,
    val justification: String?,
    val files: List<String>,
    val extraData: Any?,
    val createdAt: OffsetDateTime
)