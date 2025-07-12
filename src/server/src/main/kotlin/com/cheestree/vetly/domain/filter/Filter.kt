package com.cheestree.vetly.domain.filter

data class Filter<T>(
    val path: String,
    val value: T?,
    val operation: Operation,
    val caseInsensitive: Boolean = true
)
