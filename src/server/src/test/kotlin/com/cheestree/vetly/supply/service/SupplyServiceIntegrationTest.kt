package com.cheestree.vetly.supply.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.http.model.input.supply.SupplyQueryInputModel
import com.cheestree.vetly.service.SupplyService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SupplyServiceIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var supplyService: SupplyService

    @Nested
    inner class GetAllRequestTests {
        @Test
        fun `should retrieve all supplies successfully`() {
            val requests = supplyService.getSupplies(query = SupplyQueryInputModel())

            assertThat(requests.elements).hasSize(savedSupplies.size)
        }

        @Test
        fun `should filter supplies by type`() {
            val supplies =
                supplyService.getSupplies(
                    query =
                        SupplyQueryInputModel(
                            type = SupplyType.LIQUID,
                        ),
                )

            assertThat(supplies.elements).hasSize(1)
            assertThat(supplies.elements.first().name).isEqualTo("Dewormer L")
        }

        @Test
        fun `should filter supplies by name`() {
            val requestsInRange =
                supplyService.getSupplies(
                    query =
                        SupplyQueryInputModel(
                            name = "Dewor",
                        ),
                )

            assertThat(requestsInRange.elements).hasSize(1)
            assertThat(requestsInRange.elements.first().name).isEqualTo("Dewormer L")
        }
    }
}
