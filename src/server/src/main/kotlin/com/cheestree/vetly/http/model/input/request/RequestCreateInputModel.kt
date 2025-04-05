package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget

data class RequestCreateInputModel(
    val action: RequestAction,
    val target: RequestTarget,
    val justification: String,
    val extraData: String?,
    val files: List<String>
)