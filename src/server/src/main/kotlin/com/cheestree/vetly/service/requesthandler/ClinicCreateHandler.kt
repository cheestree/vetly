package com.cheestree.vetly.service.requesthandler

import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestHandler
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.service.ClinicService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ClinicCreateHandler(
    private val clinicService: ClinicService,
    private val objectMapper: ObjectMapper,
) : RequestHandler {
    override fun canHandle(
        target: RequestTarget,
        action: RequestAction,
    ): Boolean = target == RequestTarget.CLINIC && action == RequestAction.CREATE

    override fun execute(request: Request) {
        val clinicInput =
            try {
                objectMapper.convertValue(request.extraData, ClinicCreateInputModel::class.java)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid input data for clinic creation: ${e.message}")
            }
        clinicService.createClinic(
            name = clinicInput.name,
            address = clinicInput.address,
            phone = clinicInput.phone,
            email = clinicInput.email,
            nif = clinicInput.nif,
            lat = clinicInput.lat,
            lng = clinicInput.lng,
            imageUrl = clinicInput.imageUrl,
            ownerId = clinicInput.ownerId,
        )
    }
}
