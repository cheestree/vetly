package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import org.springframework.data.jpa.repository.JpaRepository

interface SupplyRepository: JpaRepository<MedicalSupply, Long>