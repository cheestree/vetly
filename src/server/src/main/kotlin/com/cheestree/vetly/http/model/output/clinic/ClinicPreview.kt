package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType

data class ClinicPreview(
    val id: Long,
    val name: String,
    val address: String,
    val phone: String,
    val openingHours: List<OpeningHourInformation>,
    val imageUrl: String?,
    val services: List<ServiceType>
)
