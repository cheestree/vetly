package com.cheestree.vetly

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.CheckupRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.repository.MedicalSupplyRepository
import com.cheestree.vetly.repository.RequestRepository
import com.cheestree.vetly.repository.RoleRepository
import com.cheestree.vetly.repository.SupplyRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.UserRoleRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {
    @Autowired lateinit var animalRepository: AnimalRepository

    @Autowired lateinit var clinicRepository: ClinicRepository

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var roleRepository: RoleRepository

    @Autowired lateinit var userRoleRepository: UserRoleRepository

    @Autowired lateinit var checkupRepository: CheckupRepository

    @Autowired lateinit var supplyRepository: SupplyRepository

    @Autowired lateinit var medicalSupplyRepository: MedicalSupplyRepository

    @Autowired lateinit var guideRepository: GuideRepository

    @Autowired lateinit var requestRepository: RequestRepository

    lateinit var savedAnimals: List<Animal>
    lateinit var savedClinics: List<Clinic>
    lateinit var savedUsers: List<User>
    lateinit var savedRoles: List<RoleEntity>
    lateinit var savedCheckups: List<Checkup>
    lateinit var savedGuides: List<Guide>
    lateinit var savedRequests: List<Request>
    lateinit var savedSupplies: List<MedicalSupply>
    lateinit var savedClinicSupplies: List<MedicalSupplyClinic>

    protected val nonExistentNumber = 9999L
    protected val nonExistentUuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    @BeforeEach
    fun setup() {
        // 1. Save basic entities without relationships
        val users = userRepository.saveAll(TestDataFactory.users())
        val roles = roleRepository.saveAll(TestDataFactory.roles())
        val clinics = clinicRepository.saveAll(TestDataFactory.clinics())

        // 2. Create UserRoles
        @Transactional
        fun createUserRoles() {
            val freshUsers = userRepository.findAllById(users.map { it.id })
            val freshRoles = roleRepository.findAllById(roles.map { it.id })

            val userRoles =
                freshUsers.zip(freshRoles).map { (user, role) ->
                    UserRole(
                        id = UserRoleId(user.id, role.id),
                        user = user,
                        role = role,
                    ).also { userRole ->
                        user.roles.add(userRole)
                    }
                }

            userRoleRepository.saveAll(userRoles)
            userRepository.saveAll(freshUsers)
        }

        createUserRoles()

        // 3. Re-fetch everything with fresh relationships
        this.savedUsers = userRepository.findAll()
        this.savedRoles = roles
        this.savedClinics = clinics

        // 4. Create dependent entities
        val animals = animalRepository.saveAll(TestDataFactory.animals(savedUsers))
        this.savedAnimals = animals

        val checkups = checkupRepository.saveAll(TestDataFactory.checkups(animals, clinics, savedUsers))
        this.savedCheckups = checkups

        val supplies = medicalSupplyRepository.saveAll(TestDataFactory.supplies())
        val clinicSupplies = supplyRepository.saveAll(TestDataFactory.clinicSupplies(supplies, clinics))
        this.savedSupplies = supplies
        this.savedClinicSupplies = clinicSupplies

        val guides = guideRepository.saveAll(TestDataFactory.guides(savedUsers))
        this.savedGuides = guides

        val requests = requestRepository.saveAll(TestDataFactory.requests(savedUsers))
        this.savedRequests = requests
    }
}
