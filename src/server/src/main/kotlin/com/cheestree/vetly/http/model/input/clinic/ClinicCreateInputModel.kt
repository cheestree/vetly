package com.cheestree.vetly.http.model.input.clinic

data class ClinicCreateInputModel(
    val name: String,
    val nif: String,
    val address: String,
    val lng: Double,
    val lat: Double,
    val phone: String,
    val email: String,
    val imageUrl: String?,
    val ownerId: Long?
)