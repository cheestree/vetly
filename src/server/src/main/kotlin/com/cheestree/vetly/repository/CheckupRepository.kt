package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.checkup.Checkup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CheckupRepository : JpaRepository<Checkup, Long>, JpaSpecificationExecutor<Checkup> {
    fun deleteCheckupById(id: Long): Boolean
    fun findByIdAndAnimal_Owner_Id(id: Long, animalOwnerId: Long): Optional<Checkup>
}