package com.cheestree.vetly.http.model.output.clinic

data class OpeningHourInformation(
    val weekday: Int,
    val opensAt: String,
    val closesAt: String,
)
