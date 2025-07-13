package com.cheestree.vetly.http.model.input.guide

import org.springframework.data.domain.Sort

data class GuideQueryInputModel(
    val title: String? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "title",
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
)
