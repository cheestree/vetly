package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface RequestRepository : JpaRepository<Request, UUID>, JpaSpecificationExecutor<Request> {
    fun getRequestById(id: UUID): Optional<Request>
    fun getRequestByActionAndTarget(action: RequestAction, target: RequestTarget): Optional<Request>
    fun deleteRequestById(id: UUID): Boolean
}