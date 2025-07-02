package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.checkup.Checkup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CheckupRepository :
    JpaRepository<Checkup, Long>,
    JpaSpecificationExecutor<Checkup>
