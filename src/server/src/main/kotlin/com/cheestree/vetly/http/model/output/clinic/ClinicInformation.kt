package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.http.model.output.user.UserPreview

data class ClinicInformation(
    val name: String,
    val address: String,
    val long: Double,
    val lat: Double,
    val phone: String,
    val email: String,
    val imageUrl: String?,
    val owner: UserPreview?
)