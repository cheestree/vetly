package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.ClinicApi
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicQueryInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.CreatedResourceResponse
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.ClinicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
class ClinicController(
    private val clinicService: ClinicService,
) : ClinicApi {
    @AuthenticatedRoute
    override fun getClinics(query: ClinicQueryInputModel): ResponseEntity<ResponseList<ClinicPreview>> =
        ResponseEntity.ok(clinicService.getAllClinics(query))

    @AuthenticatedRoute
    override fun getClinic(id: Long): ResponseEntity<ClinicInformation> = ResponseEntity.ok(clinicService.getClinic(id))

    @ProtectedRoute(ADMIN)
    override fun createClinic(
        user: AuthenticatedUser,
        createdClinic: ClinicCreateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<CreatedResourceResponse<Long>> {
        val clinic = clinicService.createClinic(createdClinic, image)
        val location = URI.create("${Path.Clinics.BASE}/${clinic.id}")

        return ResponseEntity.created(location).body(CreatedResourceResponse(clinic.id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateClinic(
        id: Long,
        updatedClinic: ClinicUpdateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<ClinicInformation> = ResponseEntity.ok(clinicService.updateClinic(id, updatedClinic, image))

    @ProtectedRoute(ADMIN)
    override fun deleteClinic(id: Long): ResponseEntity<Unit> {
        clinicService.deleteClinic(id)
        return ResponseEntity.noContent().build()
    }
}
