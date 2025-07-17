package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.http.model.output.file.FileInformation
import com.cheestree.vetly.http.model.output.user.UserPreview

data class ClinicInformation(
    val id: Long,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val phone: String,
    val email: String,
    val openingHours: List<OpeningHourInformation>,
    val image: FileInformation?,
    val services: Set<ServiceType>,
    val owner: UserPreview?,
)
