package com.cheestree.vetly.http.model.input.clinic

import org.hibernate.validator.constraints.Range
import java.time.LocalTime

data class OpeningHourInputModel(
    @field:Range(min = 0, max = 6, message = "Animal ID must be greater than 0")
    val weekday: Int,
    val opensAt: LocalTime,
    val closesAt: LocalTime,
)
