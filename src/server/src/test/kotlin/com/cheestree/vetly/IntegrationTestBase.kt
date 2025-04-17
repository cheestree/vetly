package com.cheestree.vetly

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.repository.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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

    protected val nonExistentNumber = 9999L

    @BeforeEach
    fun setup() {
        val users = userRepository.saveAll(TestDataFactory.users())
        val roles = roleRepository.saveAll(TestDataFactory.roles())
        val animals = TestDataFactory.animals(users)
        val clinics = TestDataFactory.clinics()

        val userRoles = TestDataFactory.userRoles(users, roles)
        userRoleRepository.saveAll(userRoles)

        val savedClinics = clinicRepository.saveAll(clinics)
        val savedAnimals = animalRepository.saveAll(animals)
        val savedUsers = userRepository.findAll() // Re-fetch with roles

        this.savedAnimals = savedAnimals
        this.savedClinics = savedClinics
        this.savedUsers = savedUsers
        this.savedRoles = roles

        val checkups = TestDataFactory.checkups(savedAnimals, savedClinics, savedUsers)
        this.savedCheckups = checkupRepository.saveAll(checkups)

        val supplies = TestDataFactory.supplies()
        val savedSupplies = medicalSupplyRepository.saveAll(supplies)

        val clinicSupplies = TestDataFactory.clinicSupplies(savedSupplies, savedClinics)
        supplyRepository.saveAll(clinicSupplies)

        val guides = TestDataFactory.guides(savedUsers)
        val savedGuides = guideRepository.saveAll(guides)
        this.savedGuides = savedGuides

        val requests = TestDataFactory.requests(savedUsers)
        requestRepository.saveAll(requests)
    }
}
