package com.cheestree.vetly.controllers

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.path.Path.Clinics.GET_ALL
import com.cheestree.vetly.services.ClinicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClinicController(
    private val clinicService: ClinicService
) {
    @GetMapping(GET_ALL)
    fun getClinics(): ResponseEntity<List<Clinic>> {
        return ResponseEntity.ok(clinicService.getClinics())
    }
}