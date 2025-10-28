package com.cheestree.vetly.repository.clinic

import com.cheestree.vetly.domain.clinic.Clinic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ClinicRepository :
    JpaRepository<Clinic, Long>,
    JpaSpecificationExecutor<Clinic> {
    fun existsByNif(nif: String): Boolean

    fun findByLongitudeAndLatitude(
        lng: Double,
        lat: Double,
    ): MutableList<Clinic>
}
