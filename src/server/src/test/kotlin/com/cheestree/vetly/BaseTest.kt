package com.cheestree.vetly

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinicId
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals

open class BaseTest {
    val animalsBase = listOf(
        Animal(1L, "Dog", "1234567890", "Bulldog", OffsetDateTime.now().minusDays(1), null, null),
        Animal(2L, "Cat", "0987654321", "Siamese", OffsetDateTime.now().minusDays(2), null, null),
        Animal(3L, "Parrot", "1122334455", "Macaw", OffsetDateTime.now().minusDays(3), null, null),
        Animal(4L, "Rabbit", "2233445566", "Angora", OffsetDateTime.now().minusDays(4), null, null)
    )

    private val adminRole = RoleEntity(id = 1L, role = Role.ADMIN)
    private val veterinarianRole = RoleEntity(id = 1L, role = Role.VETERINARIAN)

    private val user1 = User(1L, UUID.randomUUID(), "", "Dr. John Doe", "john.doe@example.com", roles = emptySet())
    private val user2 = User(2L, UUID.randomUUID(), "", "Dr. Jane Smith", "jane.smith@example.com", roles = emptySet())
    private val user3 = User(3L, UUID.randomUUID(), "", "Dr. Janette Smith", "janette.smith@example.com", roles = emptySet())

    private val userRole1 = UserRole(id = UserRoleId(userId = user1.id, roleId = adminRole.id), user = user1, role = adminRole)
    private val userRole2 = UserRole(id = UserRoleId(userId = user2.id, roleId = veterinarianRole.id), user = user2, role = veterinarianRole)
    private val userRole3 = UserRole(id = UserRoleId(userId = user3.id, roleId = veterinarianRole.id), user = user3, role = veterinarianRole)

    val userWithAdmin = User(user1.id, user1.uuid, username = user1.username, email = user1.email, roles = setOf(userRole1))
    private val userWithVet1 = User(user2.id, user2.uuid, username = user2.username, email = user2.email, roles = setOf(userRole2))
    private val userWithVet2 = User(user3.id, user3.uuid, username = user3.username, email = user3.email, roles = setOf(userRole3))

    val veterinariansBase = listOf(
        userWithVet1,
        userWithVet2,
    )

    val clinicsBase = listOf(
        Clinic(1L, "", "Happy Pets Clinic", "123 Pet Street", 1.0, 1.0, "1234567890", "a@gmail.com"),
        Clinic(2L, "", "Healthy Animals Clinic", "456 Animal Avenue", 1.0, 2.0, "1234567880", "b@gmail.com")
    )

    private val antibioticPill = PillSupply(
        name = "Antibiotic A",
        description = "Used for bacterial infections",
        imageUrl = "https://example.com/antibiotic-a.png",
        pillsPerBox = 30,
        mgPerPill = 500.0
    ).copy(id = 101L)

    private val dewormerLiquid = LiquidSupply(
        name = "Dewormer L",
        description = "Deworming treatment",
        imageUrl = "https://example.com/dewormer-l.png",
        mlPerBottle = 100.0,
        mlDosePerUse = 5.0,
    ).copy(id = 102L)

    private val rabiesShot = ShotSupply(
        name = "Rabies Vaccine",
        description = "Prevents rabies in animals",
        imageUrl = "https://example.com/rabies-vaccine.png",
        vialsPerBox = 10,
        mlPerVial = 1.0
    ).copy(id = 103L)

    val supplyClinicBase = listOf(
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(medicalSupply = antibioticPill.id, clinic = clinicsBase[0].id),
            medicalSupply = antibioticPill,
            clinic = clinicsBase[0],
            price = BigDecimal("10.99"),
            count = 100
        ),
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(medicalSupply = dewormerLiquid.id, clinic = clinicsBase[0].id),
            medicalSupply = dewormerLiquid,
            clinic = clinicsBase[0],
            price = BigDecimal("14.50"),
            count = 50
        ),
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(medicalSupply = rabiesShot.id, clinic = clinicsBase[1].id),
            medicalSupply = rabiesShot,
            clinic = clinicsBase[1],
            price = BigDecimal("23.00"),
            count = 25
        )
    )

    val checkupsBase = listOf(
        Checkup(
            id = 1L,
            description = "Routine checkup",
            dateTime = OffsetDateTime.now().minusDays(1),
            clinic = clinicsBase[0],
            veterinarian = veterinariansBase[0],
            animal = animalsBase.first { it.id == 1L }
        ),
        Checkup(
            id = 2L,
            description = "Vaccination",
            dateTime = OffsetDateTime.now().minusDays(2),
            clinic = clinicsBase[1],
            veterinarian = veterinariansBase[1],
            animal = animalsBase.first { it.id == 2L }
        )
    )

    val guidesBase = listOf(
        Guide(
            id = 1L, title = "Dog Care", description = "Guide on dog care",
            imageUrl = null, content = "Content about dog care",
            createdAt = OffsetDateTime.now().minusDays(1),
            modifiedAt = null, user = veterinariansBase[0]
        ),
        Guide(
            id = 2L, title = "Cat Nutrition", description = "Guide on cat nutrition",
            imageUrl = null, content = "Content about cat nutrition",
            createdAt = OffsetDateTime.now().minusDays(2),
            modifiedAt = null, user = veterinariansBase[1]
        )
    )

    val requestsBase = listOf(
        Request(
            action = RequestAction.CREATE,
            target = RequestTarget.CLINIC,
            justification = "Because I want to",
            submittedAt = OffsetDateTime.now().minusDays(1),
            extraData = "{\"name\": \"New Clinic\", \"address\": \"123 New Street\"}",
            user = userWithAdmin,
            files = listOf()
        ),
        Request(
            action = RequestAction.UPDATE,
            target = RequestTarget.CLINIC,
            justification = "Because I want to but update",
            submittedAt = OffsetDateTime.now().minusDays(2),
            extraData = "{\"name\": \"Updated Clinic\", \"address\": \"456 Updated Street\"}",
            user = userWithVet1,
            files = listOf()
        )
    )

    companion object {
        val mapper: ObjectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        fun ResultActions.andExpectErrorResponse(
            expectedStatus: HttpStatus,
            expectedMessage: String,
            expectedError: String
        ): ResultActions {
            return this.andExpect(status().`is`(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.error").value(expectedError))
        }

        inline fun <reified T> ResultActions.andExpectSuccessResponse(
            expectedStatus: HttpStatus,
            expectedMessage: String? = null,
            expectedData: T? = null
        ): ResultActions {
            if(expectedMessage == null) return this

            return this.andExpect(status().`is`(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo {
                    val responseString = it.response.contentAsString
                    val responseBody: T = mapper.readValue(responseString)

                    assertEquals(expectedData, responseBody)
                }
        }

        fun Any.toJson(): String = mapper.writeValueAsString(this)
    }
}