package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.AnimalController
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
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

    private lateinit var animals: List<Animal>
    private lateinit var user : AuthenticatedUser

    @BeforeTest
    fun setup() {
        user = userWithAdmin.toAuthenticatedUser()

        animals = animalsBase

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

    private fun performGetAllAnimalsRequest(
        isVet: Boolean = false,
        params: Map<String, String> = emptyMap()
    ): ResultActions {
        val path = if (isVet) {
            Path.Animals.GET_ALL
        } else {
            Path.Animals.GET_USER_ANIMALS
        }

        val request = get(path).apply {
            params.forEach { (key, value) -> param(key, value) }
        }

        return mockMvc.perform(request)
    }

    private fun assertGetAllAnimalsSuccess(
        isVet: Boolean = false,
        userId: Long? = null,
        params: Map<String, String> = emptyMap(),
        expectedAnimals: List<AnimalPreview>
    ) {
        val pageable = PageRequest.of(0, 10)
        val expectedPage = PageImpl(expectedAnimals, pageable, expectedAnimals.size.toLong())

        every {
            animalService.getAllAnimals(
                userId = if (!isVet) userId else any(),
                name = any(),
                microchip = any(),
                birthDate = any(),
                species = any(),
                owned = any(),
                page = any(),
                size = any(),
                sortBy = any(),
                sortDirection = any()
            )
        } returns expectedPage

        performGetAllAnimalsRequest(isVet = isVet, params = params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedPage)
    }

    @Nested
    inner class GetAllAnimalTests {
        @Test
        fun `should return 200 if animals found with name filter`() {
            val expectedAnimals = animals.filter { it.name.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
            assertGetAllAnimalsSuccess(isVet = true, expectedAnimals = expectedAnimals, params = mapOf("name" to "Dog"))
        }

        @Test
        fun `should return 200 if animals found with birthDate filter`() {
            val birthDate = OffsetDateTime.now().minusDays(2).toString()
            val expectedAnimals = animals.filter { it.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) ?: false }.map { it.asPreview() }
            assertGetAllAnimalsSuccess(isVet = true, expectedAnimals = expectedAnimals, params = mapOf("birthDate" to birthDate))
        }

        @Test
        fun `should return 200 if animals found with sort direction ASC`() {
            val expectedAnimals = animals.sortedBy { it.name }.map { it.asPreview() }
            assertGetAllAnimalsSuccess(isVet = true, expectedAnimals = expectedAnimals, params = mapOf("sortDirection" to "ASC"))
        }

        @Test
        fun `should return 200 if animals found on GET_ALL`() {
            val expectedAnimals = animals.map { it.asPreview() }
            assertGetAllAnimalsSuccess(isVet = true, expectedAnimals = expectedAnimals)
        }
    }

    @Nested
    inner class GetAnimalTests {
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
    }

    @Nested
    inner class CreateAnimalTests {
        @Test
        fun `should return 409 if animal microchip exists on CREATE`(){
            val animalId = 1L
            val expectedAnimal = animals.first { it.id == animalId }
            val createdAnimal = AnimalCreateInputModel(
                name = expectedAnimal.name,
                microchip = expectedAnimal.microchip,
                birthDate = expectedAnimal.birthDate,
                species = expectedAnimal.species,
                imageUrl = expectedAnimal.imageUrl
            )

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
                    .content(createdAnimal.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.CONFLICT,
                expectedMessage = "Resource already exists: Animal with microchip ${expectedAnimal.microchip} already exists",
                expectedError = "Resource already exists"
            )
        }

        @Test
        fun `should return 200 if animal created successfully`() {
            val expectedAnimal = animals.first()
            val createdAnimal = AnimalCreateInputModel(
                name = expectedAnimal.name,
                microchip = expectedAnimal.microchip,
                birthDate = expectedAnimal.birthDate,
                species = expectedAnimal.species,
                imageUrl = expectedAnimal.imageUrl
            )

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
                    .content(createdAnimal.toJson())
            ).andExpectSuccessResponse<Map<String, Long>>(
                expectedStatus = HttpStatus.CREATED,
                expectedMessage = null,
                expectedData = mapOf("id" to expectedAnimal.id)
            )
        }
    }

    @Nested
    inner class UpdateAnimalTests {
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
            val updatedAnimal = AnimalUpdateInputModel(
                name = "Dog", null, null, null, null,
            )

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
                    .content(updatedAnimal.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Animal not found",
                expectedError = "Resource not found"
            )
        }

        @Test
        fun `should return 200 if animal updated successfully`() {
            val expectedAnimal = animals.first()
            val updatedAnimal = AnimalUpdateInputModel(
                name = expectedAnimal.name,
                microchip = expectedAnimal.microchip,
                birthDate = expectedAnimal.birthDate,
                species = expectedAnimal.species,
                imageUrl = expectedAnimal.imageUrl
            )

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
                    .content(updatedAnimal.toJson())
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }

    @Nested
    inner class DeleteAnimalTests {
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
}