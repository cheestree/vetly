package com.cheestree.vetly.http.model.input.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType

data class ClinicUpdateInputModel(
    val name: String?,
    val nif: String?,
    val address: String?,
    val lng: Double?,
    val lat: Double?,
    val phone: String?,
    val email: String?,
    val services: List<ServiceType>?,
    val openingHours: List<OpeningHourInputModel>?,
    val ownerId: Long?,
)
