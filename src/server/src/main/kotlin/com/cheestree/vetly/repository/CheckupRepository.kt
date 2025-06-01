package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.checkup.Checkup
import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CheckupRepository :
    JpaRepository<Checkup, Long>,
    JpaSpecificationExecutor<Checkup> {
    @Query("SELECT c FROM Checkup c LEFT JOIN FETCH c.files WHERE c.id = :id")
    fun findWithFilesById(@Param("id") id: Long): Optional<Checkup>
    }
