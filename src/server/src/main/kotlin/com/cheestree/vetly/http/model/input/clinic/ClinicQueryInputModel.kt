package com.cheestree.vetly.http.model.input.clinic

import org.springframework.data.domain.Sort

data class ClinicQueryInputModel(
    val name: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "name",
    val sortDirection: Sort.Direction = Sort.Direction.DESC
)