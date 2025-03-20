package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.clinic.Clinic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClinicRepository : JpaRepository<Clinic, Int> {
}