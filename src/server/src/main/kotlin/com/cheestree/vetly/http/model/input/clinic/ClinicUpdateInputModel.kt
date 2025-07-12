package com.cheestree.vetly.http.model.input.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Range

data class ClinicUpdateInputModel(
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Name can only contain letters, spaces, and hyphens")
    val name: String? = null,
    val nif: String? = null,
    val address: String? = null,
    @field:Range(min = -180, max = 180, message = "Longitude must be between -180 and 180")
    val lng: Double? = null,
    @field:Range(min = -90, max = 90, message = "Latitude must be between -90 and 90")
    val lat: Double? = null,
    val phone: String? = null,
    @field:Email(message = "Email is required")
    val email: String? = null,
    val services: List<ServiceType>? = null,
    val openingHours: List<OpeningHourInputModel>? = null,
    @field:Email(message = "Owner email is required")
    val ownerEmail: String? = null
)
