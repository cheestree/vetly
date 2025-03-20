package com.cheestree.vetly.services

import com.cheestree.vetly.repository.CheckupRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ClinicService(
    private val clinicRepository: ClinicRepository,
    private val userRepository: UserRepository,
){
    fun getClinics() = clinicRepository.findAll().toList()
}