package com.cheestree.vetly.components

import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.service.ClinicService
import com.cheestree.vetly.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class RequestExecutor(
    private val objectMapper: ObjectMapper,
    private val clinicService: ClinicService,
    private val userService: UserService
) {

    fun execute(request: Request) {
        val (target, action) = request.target to request.action

        when (target to action) {
            RequestTarget.CLINIC to RequestAction.CREATE -> {
                val clinicInput = objectMapper.convertValue(request.extraData, ClinicCreateInputModel::class.java)
                clinicService.createClinic(
                    name = clinicInput.name,
                    address = clinicInput.address,
                    phone = clinicInput.phone,
                    email = clinicInput.email,
                    nif = clinicInput.nif,
                    lat = clinicInput.lat,
                    lng = clinicInput.lng,
                    imageUrl = clinicInput.imageUrl,
                    ownerId = clinicInput.ownerId
                )
            }

            RequestTarget.ROLE to RequestAction.UPDATE -> {
                val roleEnum = objectMapper.convertValue(request.extraData, Role::class.java)
                userService.updateUserRole(request.user.id, roleEnum)
            }

            //  ...

            else -> throw VetException.BadRequestException("Unsupported request target/action: ${target.name} to ${action.name}")
        }
    }
}