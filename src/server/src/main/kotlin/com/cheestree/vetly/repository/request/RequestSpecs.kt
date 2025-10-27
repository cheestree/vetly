package com.cheestree.vetly.repository.request

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.repository.BaseSpecs
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object RequestSpecs {
    fun byUserId(userId: Long?, roles: Set<Role>): Specification<Request> = Specification { root, query, cb ->
        val isAdmin = roles.contains(Role.ADMIN)
        if (isAdmin) {
            BaseSpecs.equalObjectLong<Request>(userId, "user", "id").toPredicate(root, query, cb)
        } else {
            cb.conjunction()
        }
    }

    fun byStatus(status: RequestStatus?) = BaseSpecs.equalEnum<Request, RequestStatus>(status, "status")

    fun byAction(action: RequestAction?) = BaseSpecs.equalEnum<Request, RequestAction>(action, "action")

    fun byTarget(target: RequestTarget?) = BaseSpecs.equalEnum<Request, RequestTarget>(target, "target")

    fun byCreatedAt(from: LocalDate?, to: LocalDate?) = BaseSpecs.betweenDates<Request>(
        from,
        to,
        "createdAt"
    )

    fun byUserName(userName: String?, roles: Set<Role>): Specification<Request> = Specification { root, query, cb ->
        val isAdmin = roles.contains(Role.ADMIN)
        if (isAdmin && !userName.isNullOrBlank()) {
            BaseSpecs.likeObjectString<Request>(userName, "user", "name").toPredicate(root, query, cb)
        } else {
            cb.conjunction()
        }
    }
}