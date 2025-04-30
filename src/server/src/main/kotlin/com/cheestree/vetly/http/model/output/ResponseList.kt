package com.cheestree.vetly.http.model.output

data class ResponseList<T>(
    val elements: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int,
    val hasNext: Boolean = page < totalPages - 1,
    val hasPrevious: Boolean = page > 0,
)
