package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.medicalsupply.MedicalSupply
import org.springframework.data.jpa.repository.JpaRepository

interface SupplyRepository: JpaRepository<MedicalSupply, Long> {
}