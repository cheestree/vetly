package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.service.SupplyService
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class SupplyServiceTest : IntegrationTestBase() {
    @Autowired
    private lateinit var supplyService: SupplyService

    @Nested
    inner class GetAllRequestTests {
        @Test
        fun `should retrieve all supplies successfully`() {
            val requests = supplyService.getSupplies()

            assertThat(requests).hasSize(savedSupplies.size)
        }

        @Test
        fun `should filter supplies by type`() {
            val supplies = supplyService.getSupplies(type = SupplyType.LIQUID)

            assertThat(supplies).hasSize(1)
            assertThat(supplies.first().name).isEqualTo("Dewormer L")
        }

        @Test
        fun `should filter supplies by name`() {
            val requestsInRange = supplyService.getSupplies(name = "Dewor")

            assertThat(requestsInRange).hasSize(1)
            assertThat(requestsInRange.first().name).isEqualTo("Dewormer L")
        }
    }
}
