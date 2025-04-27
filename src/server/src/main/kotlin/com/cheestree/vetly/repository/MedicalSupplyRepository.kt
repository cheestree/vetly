package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface MedicalSupplyRepository : JpaRepository<MedicalSupply, Long>, JpaSpecificationExecutor<MedicalSupply>
