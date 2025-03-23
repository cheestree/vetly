package com.cheestree.vetly.controller

import com.cheestree.vetly.http.model.output.clinic.ClinicInformationOutput
import com.cheestree.vetly.http.path.Path.Clinics.GET_ALL
import com.cheestree.vetly.service.ClinicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClinicController(
    private val clinicService: ClinicService
) {
    @GetMapping(GET_ALL)
    fun getClinics(): ResponseEntity<List<ClinicInformationOutput>> {
        return ResponseEntity.ok(clinicService.getClinics())
    }
}