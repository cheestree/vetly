package com.cheestree.vetly.domain.error

data class ApiError(
    val message: String,
    val details: List<ErrorDetail>,
)
