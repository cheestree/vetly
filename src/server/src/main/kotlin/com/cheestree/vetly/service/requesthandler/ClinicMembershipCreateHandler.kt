package com.cheestree.vetly.service.requesthandler

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestHandler
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.model.input.clinic.ClinicMembershipInputModel
import com.cheestree.vetly.service.ClinicService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ClinicMembershipCreateHandler(private val clinicService: ClinicService, private val objectMapper: ObjectMapper) : RequestHandler {
    override fun canHandle(
        target: RequestTarget,
        action: RequestAction,
    ): Boolean = target == RequestTarget.CLINIC_MEMBERSHIP && action == RequestAction.CREATE || action == RequestAction.DELETE

    override fun execute(request: Request) {
        val clinicMembership =
            try {
                objectMapper.convertValue(request.extraData, ClinicMembershipInputModel::class.java)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid input data for clinic membership creation: ${e.message}")
            }
        if (request.action == RequestAction.DELETE) {
            clinicService.removeClinicMember(clinicMembership.clinicId, request.user.id)
            return
        } else {
            clinicService.addClinicMember(clinicMembership.clinicId, request.user.id)
        }
    }
}
