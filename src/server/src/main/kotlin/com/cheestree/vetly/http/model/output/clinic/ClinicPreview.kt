package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.http.model.output.file.FilePreview

data class ClinicPreview(
    val id: Long,
    val name: String,
    val address: String,
    val phone: String,
    val openingHours: List<OpeningHourInformation>,
    val image: FilePreview?,
    val services: List<ServiceType>,
)
