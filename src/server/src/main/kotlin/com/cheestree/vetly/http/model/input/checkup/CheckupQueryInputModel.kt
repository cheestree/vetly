package com.cheestree.vetly.http.model.input.checkup

import org.springframework.data.domain.Sort
import java.time.LocalDate

data class CheckupQueryInputModel(
    val veterinarianId: Long? = null,
    val veterinarianName: String? = null,
    val animalId: Long? = null,
    val animalName: String? = null,
    val clinicId: Long? = null,
    val clinicName: String? = null,
    val dateTimeStart: LocalDate? = null,
    val dateTimeEnd: LocalDate? = null,
    val title: String? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "createdAt",
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
)
