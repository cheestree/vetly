package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface RequestRepository :
    JpaRepository<Request, UUID>,
    JpaSpecificationExecutor<Request> {
    fun getRequestById(id: UUID): Optional<Request>

    fun existsRequestByActionAndTargetAndStatusAndUser_Id(action: RequestAction, target: RequestTarget, status: RequestStatus, userId: Long): Boolean
}
