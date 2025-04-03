package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.AnimalController
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.AnimalService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime
import kotlin.test.BeforeTest

@WebMvcTest(AnimalController::class)
class AnimalControllerTest: BaseTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var animalService: AnimalService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var animals: MutableSet<Animal>
    private lateinit var user : AuthenticatedUser

    @BeforeTest
    fun setup() {
        user = AuthenticatedUser(id = 1L, name = "Dummy", email = "test@example.com",
            roles = setOf(Role.ADMIN)
        )

        animals = mutableSetOf(
            Animal(1L, "Dog", "1234567890", "Bulldog", OffsetDateTime.now().minusDays(1),
                null, null),
            Animal(2L, "Cat", "0987654321", "Siamese", OffsetDateTime.now().minusDays(2),
                null, null),
            Animal(3L, "Parrot", "1122334455", "Macaw", OffsetDateTime.now().minusDays(3),
                null, null),
            Animal(4L, "Rabbit", "2233445566", "Angora", OffsetDateTime.now().minusDays(4),
                null, null)
        )

        animalService = mockk<AnimalService>()
        authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(AnimalController(animalService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `should return 200 if animals found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedAnimals = animals.map { it.asPreview() }
        val expectedPage: Page<AnimalPreview> = PageImpl(expectedAnimals, pageable, expectedAnimals.size.toLong())

        every { animalService.getAllAnimals(
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            owned = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Animals.GET_ALL)
        ).andExpectSuccessResponse<Page<AnimalPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if animals found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedAnimals = animals.filter { it.name.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
        val expectedPage: Page<AnimalPreview> = PageImpl(expectedAnimals, pageable, expectedAnimals.size.toLong())

        every { animalService.getAllAnimals(
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            owned = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Animals.GET_ALL).param("name", "Dog")
        ).andExpectSuccessResponse<Page<AnimalPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if animals found with birthDate filter`() {
        val pageable = PageRequest.of(0, 10)
        val birthDate = OffsetDateTime.now().minusDays(2).toString()
        val expectedAnimals = animals.filter { it.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) ?: false }.map { it.asPreview() }
        val expectedPage: Page<AnimalPreview> = PageImpl(expectedAnimals, pageable, expectedAnimals.size.toLong())

        every { animalService.getAllAnimals(
            name = any(),
            microchip = any(),
            birthDate = OffsetDateTime.parse(birthDate),
            species = any(),
            owned = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Animals.GET_ALL).param("birthDate", birthDate)
        ).andExpectSuccessResponse<Page<AnimalPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if animals found with sort direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        val expectedAnimals = animals.sortedBy { it.name }.map { it.asPreview() }
        val expectedPage: Page<AnimalPreview> = PageImpl(expectedAnimals, pageable, expectedAnimals.size.toLong())

        every { animalService.getAllAnimals(
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            owned = any(),
            page = any(),
            size = any(),
            sortBy = "name",
            sortDirection = Sort.Direction.ASC
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Animals.GET_ALL)
                .param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<AnimalPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 400 if animalId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Animals.GET, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: animalId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if animal not found on GET`() {
        val animalId = 1L
        every { animalService.getAnimal(
            animalId = any(),
        ) } throws ResourceNotFoundException("Animal not found")

        mockMvc.perform(
            get(Path.Animals.GET, animalId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Animal not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if animal found on GET`() {
        val animalId = 1L
        val expectedAnimal = animals.first { it.id == animalId }

        every { animalService.getAnimal(
            animalId = any(),
        ) } returns expectedAnimal.asPublic()

        mockMvc.perform(
            get(Path.Animals.GET, animalId)
        ).andExpectSuccessResponse<AnimalInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedAnimal.asPublic()
        )
    }

    @Test
    fun `should return 409 if animal microchip exists on CREATE`(){
        val animalId = 1L
        val expectedAnimal = animals.first { it.id == animalId }

        every { animalService.createAnimal(
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            imageUrl = any()
        ) } throws ResourceAlreadyExistsException("Animal with microchip ${expectedAnimal.microchip} already exists")

        mockMvc.perform(
            post(Path.Animals.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AnimalCreateInputModel(
                    name = expectedAnimal.name,
                    microchip = expectedAnimal.microchip,
                    birthDate = expectedAnimal.birthDate,
                    species = expectedAnimal.species,
                    imageUrl = expectedAnimal.imageUrl
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.CONFLICT,
            expectedMessage = "Resource already exists: Animal with microchip ${expectedAnimal.microchip} already exists",
            expectedError = "Resource already exists"
        )
    }

    @Test
    fun `should return 200 if animal created successfully`() {
        val expectedAnimal = animals.first()

        every { animalService.createAnimal(
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            imageUrl = any()
        ) } returns expectedAnimal.id

        mockMvc.perform(
            post(Path.Animals.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AnimalCreateInputModel(
                    name = expectedAnimal.name,
                    microchip = expectedAnimal.microchip,
                    birthDate = expectedAnimal.birthDate,
                    species = expectedAnimal.species,
                    imageUrl = expectedAnimal.imageUrl
                ).toJson())
        ).andExpectSuccessResponse<Map<String, Long>>(
            expectedStatus = HttpStatus.CREATED,
            expectedMessage = null,
            expectedData = mapOf("id" to expectedAnimal.id)
        )
    }

    @Test
    fun `should return 400 if animalId is invalid on UPDATE`() {
        mockMvc.perform(
            put(Path.Animals.UPDATE, "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AnimalUpdateInputModel(
                    name = "Dog", null, null, null, null,
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: animalId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if animal not found on UPDATE`() {
        val animalId = 1L
        every { animalService.updateAnimal(
            id = any(),
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            imageUrl = any()
        ) } throws ResourceNotFoundException("Animal not found")

        mockMvc.perform(
            put(Path.Animals.UPDATE, animalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AnimalUpdateInputModel(
                    name = "Dog", null, null, null, null,
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Animal not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if animal updated successfully`() {
        val expectedAnimal = animals.first()

        every { animalService.updateAnimal(
            id = any(),
            name = any(),
            microchip = any(),
            birthDate = any(),
            species = any(),
            imageUrl = any()
        ) } returns expectedAnimal.asPublic()

        mockMvc.perform(
            put(Path.Animals.UPDATE, expectedAnimal.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(AnimalUpdateInputModel(
                    name = expectedAnimal.name,
                    microchip = expectedAnimal.microchip,
                    birthDate = expectedAnimal.birthDate,
                    species = expectedAnimal.species,
                    imageUrl = expectedAnimal.imageUrl
                ).toJson())
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }

    @Test
    fun `should return 400 if animalId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Animals.DELETE, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: animalId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if animal not found on DELETE`() {
        val animalId = 1L
        every { animalService.deleteAnimal(animalId) } throws ResourceNotFoundException("Animal not found")

        mockMvc.perform(
            delete(Path.Animals.DELETE, animalId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Animal not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 204 if animal deleted successfully`() {
        val animalId = 1L
        every { animalService.deleteAnimal(animalId) } returns true

        mockMvc.perform(
            delete(Path.Animals.DELETE, animalId)
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }
}