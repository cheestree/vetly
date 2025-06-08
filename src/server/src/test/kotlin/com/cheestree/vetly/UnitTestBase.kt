package com.cheestree.vetly

import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.daysFromNow
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinicId
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.domain.user.userrole.types.AdminRole
import com.cheestree.vetly.domain.user.userrole.types.VeterinarianRole
import java.math.BigDecimal
import java.util.UUID

open class UnitTestBase {
    val animalsBase =
        listOf(
            Animal(id = 1L, name = "Dog", microchip = "1234567890", species = "Bulldog", birthDate = daysAgo(1)),
            Animal(id = 2L, name = "Cat", microchip = "0987654321", species = "Siamese", birthDate = daysAgo(2)),
            Animal(id = 3L, name = "Parrot", microchip = "1122334455", species = "Macaw", birthDate = daysAgo(3)),
            Animal(id = 4L, name = "Rabbit", microchip = "2233445566", species = "Angora", birthDate = daysAgo(4)),
        )

    private val adminRole = AdminRole(id = 1L, name = Role.ADMIN.name)
    private val veterinarianRole = VeterinarianRole(id = 1L, name = Role.VETERINARIAN.name)

    private val user1 = User(1L, UUID.randomUUID(), "", "Dr. John Doe", "john.doe@example.com", roles = mutableSetOf())
    private val user2 = User(2L, UUID.randomUUID(), "", "Dr. Jane Smith", "jane.smith@example.com", roles = mutableSetOf())
    private val user3 = User(3L, UUID.randomUUID(), "", "Dr. Janette Smith", "janette.smith@example.com", roles = mutableSetOf())

    private val userRole1 = UserRole(id = UserRoleId(userId = user1.id, roleId = adminRole.id), user = user1, role = adminRole)
    private val userRole2 =
        UserRole(id = UserRoleId(userId = user2.id, roleId = veterinarianRole.id), user = user2, role = veterinarianRole)
    private val userRole3 =
        UserRole(id = UserRoleId(userId = user3.id, roleId = veterinarianRole.id), user = user3, role = veterinarianRole)

    val userWithAdmin = User(user1.id, user1.publicId, username = user1.username, email = user1.email, roles = mutableSetOf(userRole1))
    val userWithVet1 = User(user2.id, user2.publicId, username = user2.username, email = user2.email, roles = mutableSetOf(userRole2))
    private val userWithVet2 =
        User(user3.id, user3.publicId, username = user3.username, email = user3.email, roles = mutableSetOf(userRole3))

    val veterinariansBase =
        listOf(
            userWithVet1,
            userWithVet2,
        )

    val clinicsBase =
        listOf(
            Clinic(1L, "", "Happy Pets Clinic", "123 Pet Street", 1.0, 1.0, "1234567890", "a@gmail.com"),
            Clinic(2L, "", "Healthy Animals Clinic", "456 Animal Avenue", 1.0, 2.0, "1234567880", "b@gmail.com"),
        )

    private val antibioticPill =
        PillSupply(
            id = 101L,
            name = "Antibiotic A",
            description = "Used for bacterial infections",
            imageUrl = "https://example.com/antibiotic-a.png",
            pillsPerBox = 30,
            mgPerPill = 500.0,
        )

    private val dewormerLiquid =
        LiquidSupply(
            id = 102L,
            name = "Dewormer L",
            description = "Deworming treatment",
            imageUrl = "https://example.com/dewormer-l.png",
            mlPerBottle = 100.0,
            mlDosePerUse = 5.0,
        )

    private val rabiesShot =
        ShotSupply(
            id = 103L,
            name = "Rabies Vaccine",
            description = "Prevents rabies in animals",
            imageUrl = "https://example.com/rabies-vaccine.png",
            vialsPerBox = 10,
            mlPerVial = 1.0,
        )

    val supplyBase =
        listOf(
            antibioticPill,
            dewormerLiquid,
            rabiesShot,
        )

    val supplyClinicBase =
        listOf(
            MedicalSupplyClinic(
                id = MedicalSupplyClinicId(medicalSupply = antibioticPill.id, clinic = clinicsBase[0].id),
                medicalSupply = antibioticPill,
                clinic = clinicsBase[0],
                price = BigDecimal("10.99"),
                quantity = 100,
            ),
            MedicalSupplyClinic(
                id = MedicalSupplyClinicId(medicalSupply = dewormerLiquid.id, clinic = clinicsBase[0].id),
                medicalSupply = dewormerLiquid,
                clinic = clinicsBase[0],
                price = BigDecimal("14.50"),
                quantity = 50,
            ),
            MedicalSupplyClinic(
                id = MedicalSupplyClinicId(medicalSupply = rabiesShot.id, clinic = clinicsBase[1].id),
                medicalSupply = rabiesShot,
                clinic = clinicsBase[1],
                price = BigDecimal("23.00"),
                quantity = 25,
            ),
        )

    val checkupsBase =
        listOf(
            Checkup(
                id = 1L,
                title = "Routine",
                description = "Routine checkup",
                dateTime = daysFromNow(3),
                clinic = clinicsBase[0],
                veterinarian = veterinariansBase[0],
                animal = animalsBase.first { it.id == 1L },
            ),
            Checkup(
                id = 2L,
                title = "Vaccination",
                description = "Vaccination checkup",
                dateTime = daysFromNow(4),
                clinic = clinicsBase[1],
                veterinarian = veterinariansBase[1],
                animal = animalsBase.first { it.id == 2L },
            ),
        )

    val guidesBase =
        listOf(
            Guide(
                id = 1L,
                title = "Dog Care",
                description = "Guide on dog care",
                imageUrl = null,
                content = "Content about dog care",
                author = userWithAdmin,
            ).apply {
                createdAt = daysAgo(5)
                updatedAt = daysAgo(5)
            },
            Guide(
                id = 2L,
                title = "Cat Nutrition",
                description = "Guide on cat nutrition",
                imageUrl = null,
                content = "Content about cat nutrition",
                author = userWithVet1,
            ).apply {
                createdAt = daysAgo(10)
                updatedAt = daysAgo(10)
            },
        )

    val requestsBase =
        listOf(
            Request(
                action = RequestAction.CREATE,
                target = RequestTarget.CLINIC,
                justification = "Because I want to",
                extraData =
                    mapOf(
                        "name" to "New Clinic",
                        "address" to "123 New Street",
                    ),
                user = userWithAdmin,
                files = listOf(),
            ).apply {
                createdAt = daysAgo(3)
                updatedAt = daysAgo(3)
            },
            Request(
                action = RequestAction.UPDATE,
                target = RequestTarget.CLINIC,
                justification = "Because I want to but update",
                extraData =
                    mapOf(
                        "name" to "Updated Clinic",
                        "address" to "456 Updated Street",
                    ),
                user = userWithVet1,
                files = listOf(),
            ).apply {
                createdAt = daysAgo(1)
                updatedAt = daysAgo(1)
            },
        )
}
