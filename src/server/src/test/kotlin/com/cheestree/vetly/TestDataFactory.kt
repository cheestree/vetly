package com.cheestree.vetly

import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.domain.user.userrole.types.AdminRole
import com.cheestree.vetly.domain.user.userrole.types.VeterinarianRole
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinicId
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import java.math.BigDecimal
import java.util.*

object TestDataFactory {

    fun animals(
        users: List<User>
    ) = listOf(
        Animal(
            name = "Dog",
            microchip = "1234567890",
            species = "Bulldog",
            birthDate = daysAgo(1),
            owner = null,
            imageUrl = null
        ),
        Animal(
            name = "Cat",
            microchip = null,
            species = "Siamese",
            birthDate = daysAgo(2),
            owner = null,
            imageUrl = null
        ),
        Animal(
            name = "Parrot",
            microchip = "1122334455",
            species = "Macaw",
            birthDate = daysAgo(3),
            owner = null,
            imageUrl = null
        ),
        Animal(
            name = "Rabbit",
            microchip = "2233445566",
            species = "Angora",
            birthDate = daysAgo(4),
            owner = users[0],
            imageUrl = null
        )
    )

    fun clinics() = listOf(
        Clinic(
            name = "Happy Pets",
            address = "123 Pet Street",
            lat = 1.0,
            lng = 1.0,
            phone = "1234567890",
            email = "a@gmail.com",
            nif = "123456788",
        ),
        Clinic(
            name = "Healthy Animals",
            address = "456 Animal Ave",
            lat = 1.0,
            lng = 2.0,
            phone = "1234567880",
            email = "b@gmail.com",
            nif = "123456789",
        )
    )

    fun roles() = listOf(
        AdminRole(name = Role.ADMIN.name),
        VeterinarianRole(name = Role.VETERINARIAN.name)
    )

    fun users() = listOf(
        User(
            uuid = UUID.randomUUID(),
            imageUrl = "",
            username = "Dr. John",
            email = "john@example.com",
            roles = emptySet()
        ),
        User(
            uuid = UUID.randomUUID(),
            imageUrl = "",
            username = "Dr. Jane",
            email = "jane@example.com",
            roles = emptySet()
        )
    )

    fun userRoles(users: List<User>, roles: List<RoleEntity>) = listOf(
        UserRole(
            id = UserRoleId(users[0].id, roles[0].id),
            user = users[0],
            role = roles[0]
        ),
        UserRole(
            id = UserRoleId(users[1].id, roles[1].id),
            user = users[1],
            role = roles[1]
        )
    )

    fun checkups(
        animals: List<Animal>,
        clinics: List<Clinic>,
        vets: List<User>
    ) = listOf(
        Checkup(
            description = "Routine checkup",
            dateTime = daysAgo(1),
            clinic = clinics[0],
            veterinarian = vets[0],
            animal = animals[0]
        ),
        Checkup(
            description = "Vaccination",
            dateTime = daysAgo(2),
            clinic = clinics[1],
            veterinarian = vets[1],
            animal = animals[1]
        )
    )

    fun supplies() = listOf(
        PillSupply(
            name = "Antibiotic A",
            description = "Bacterial",
            imageUrl = "url",
            pillsPerBox = 30,
            mgPerPill = 500.0
        ),
        LiquidSupply(
            name = "Dewormer L",
            description = "Worms",
            imageUrl = "url",
            mlPerBottle = 100.0,
            mlDosePerUse = 5.0
        ),
        ShotSupply(
            name = "Rabies Vaccine",
            description = "Rabies",
            imageUrl = "url",
            vialsPerBox = 10,
            mlPerVial = 1.0
        )
    )

    fun clinicSupplies(
        supplies: List<MedicalSupply>,
        clinics: List<Clinic>
    ) = listOf(
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(supplies[0].id, clinics[0].id),
            medicalSupply = supplies[0],
            clinic = clinics[0],
            price = BigDecimal("10.99"),
            quantity = 100
        ),
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(supplies[1].id, clinics[0].id),
            medicalSupply = supplies[1],
            clinic = clinics[0],
            price = BigDecimal("14.50"),
            quantity = 50
        ),
        MedicalSupplyClinic(
            id = MedicalSupplyClinicId(supplies[2].id, clinics[1].id),
            medicalSupply = supplies[2],
            clinic = clinics[1],
            price = BigDecimal("23.00"),
            quantity = 25
        )
    )

    fun guides(users: List<User>) = listOf(
        Guide(
            title = "Dog Care",
            imageUrl = null,
            description = "Dog care guide",
            content = "Content",
            createdAt = daysAgo(1),
            modifiedAt = null,
            author = users[0]
        ),
        Guide(
            title = "Cat Nutrition",
            imageUrl = null,
            description = "Cat nutrition guide",
            content = "Content",
            createdAt = daysAgo(2),
            modifiedAt = null,
            author = users[1]
        )
    )

    fun requests(users: List<User>) = listOf(
        Request(
            action = RequestAction.CREATE,
            target = RequestTarget.CLINIC,
            justification = "Just because",
            submittedAt = daysAgo(1),
            extraData = mapOf(
                "name" to "New Clinic",
                "nif" to "123455559",
                "address" to "123 New Street",
                "phone" to "1234567890",
                "lng" to 1.0,
                "lat" to 1.0,
                "email" to "baaba@gmail.com"
            ),
            user = users[0],
            files = emptyList()
        ),
        Request(
            action = RequestAction.UPDATE,
            target = RequestTarget.CLINIC,
            justification = "Why not",
            submittedAt = daysAgo(2),
            extraData = mapOf(
                "name" to "New Clinic 2",
                "nif" to "111111111",
                "address" to "122 New Street",
                "phone" to "333333333",
                "lng" to 1.0,
                "lat" to 1.0,
                "email" to "bba@gmail.com"
            ),
            user = users[1],
            files = emptyList()
        )
    )
}