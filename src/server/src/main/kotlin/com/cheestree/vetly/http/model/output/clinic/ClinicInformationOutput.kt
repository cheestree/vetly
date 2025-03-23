package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.domain.user.UserLink

data class ClinicInformationOutput(
    val name: String,
    val address: String,
    val long: Double,
    val lat: Double,
    val phone: String,
    val email: String,
    val imageUrl: String?,
    val owner: UserLink
)