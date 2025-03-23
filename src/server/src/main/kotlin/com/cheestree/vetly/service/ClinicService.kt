package com.cheestree.vetly.service

import com.cheestree.vetly.http.model.output.clinic.ClinicInformationOutput
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ClinicService(
    private val clinicRepository: ClinicRepository,
    private val userRepository: UserRepository,
){
    fun getClinics(): List<ClinicInformationOutput> {
        return clinicRepository.findAll().toList().map { it.toInformationOutput() }
    }
}