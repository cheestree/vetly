package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestStatus

data class RequestUpdateInputModel(
    val decision: RequestStatus,
    val justification: String
)