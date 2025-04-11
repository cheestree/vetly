package com.cheestree.vetly.integration.service

import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.service.ClinicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class ClinicServiceTest {
    @Autowired
    private lateinit var clinicService: ClinicService

    @Autowired
    private lateinit var clinicRepository: ClinicRepository

    
}