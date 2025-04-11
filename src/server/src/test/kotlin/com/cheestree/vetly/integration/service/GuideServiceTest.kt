package com.cheestree.vetly.integration.service

import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.service.GuideService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class GuideServiceTest {
    @Autowired
    private lateinit var guideService: GuideService

    @Autowired
    private lateinit var guideRepository: GuideRepository


}