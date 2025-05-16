package com.cheestree.vetly.service.requesthandler

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestHandler
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.model.input.user.UserRoleUpdateInputModel
import com.cheestree.vetly.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class RoleUpdateHandler(
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
) : RequestHandler {
    override fun canHandle(
        target: RequestTarget,
        action: RequestAction,
    ): Boolean = target == RequestTarget.ROLE && action == RequestAction.UPDATE

    override fun execute(request: Request) {
        val roleModel =
            try {
                objectMapper.convertValue(request.extraData, UserRoleUpdateInputModel::class.java)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid input data for role update: ${e.message}")
            }
        userService.updateUserRole(request.user.id, roleModel.roleName)
    }
}
