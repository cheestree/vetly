package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.checkup.Checkup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CheckupRepository : JpaRepository<Checkup, Long>, JpaSpecificationExecutor<Checkup> {
}