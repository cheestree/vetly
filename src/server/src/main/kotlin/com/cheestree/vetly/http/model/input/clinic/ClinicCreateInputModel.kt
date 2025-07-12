package com.cheestree.vetly.http.model.input.clinic

import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Range
import javax.annotation.Nullable

data class ClinicCreateInputModel(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Pattern(regexp = "[a-zA-Z]*", message = "Name can only have letters")
    val name: String,
    @field:NotBlank(message = "NIF cannot be blank")
    val nif: String,
    @field:NotBlank(message = "Address cannot be blank")
    val address: String,
    @field:Range(min = -180, max = 180, message = "Longitude must be between -180 and 180")
    val lng: Double,
    @field:Range(min = -90, max = 90, message = "Latitude must be between -90 and 90")
    val lat: Double,
    @field:NotBlank(message = "Phone cannot be blank")
    val phone: String,
    @field:Email(message = "Clinic email is required")
    val email: String,
    val services: Set<ServiceType>,
    val openingHours: List<OpeningHourInputModel>,
    @field:Email(message = "Owner email is required")
    val ownerEmail: String? = null
) : RequestExtraData
