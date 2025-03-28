package com.cheestree.vetly.domain.clinic

import jakarta.persistence.Embeddable

@Embeddable
data class ClinicMembershipId(
    val veterinarian: Long,
    val clinic: Long
)