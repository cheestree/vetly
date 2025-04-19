package com.cheestree.vetly.integration.service

import com.cheestree.vetly.service.SupplyService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class SupplyServiceTest {

    @Autowired
    private lateinit var supplyService: SupplyService

    
}