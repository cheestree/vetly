package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.config.JacksonConfig
import com.cheestree.vetly.controller.AnimalController
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.AnimalService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime

class AnimalControllerTestBase : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val mockMvc: MockMvc

    private val objectMapper = JacksonConfig(appConfig = AppConfig()).objectMapper()
    private val authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var animals = animalsBase
    private val animalService: AnimalService = mockk(relaxed = true)

    private val invalidAnimalId = "invalid"
    private val validAnimalId = 1L
    private val missingAnimalId = 100L
    private val validUserId = 1L

    init {
        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(AnimalController(animalService = animalService))
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun performGetAllAnimalsRequest(
        isVet: Boolean = false,
        userId: Long? = null,
        params: Map<String, String> = emptyMap(),
    ): ResultActions {
        val path =
            if (isVet) {
                Path.Animals.GET_ALL
            } else {
                requireNotNull(userId) { "userId must be provided for user route" }
                Path.Animals.GET_USER_ANIMALS.replace("{userId}", userId.toString())
            }

        val request =
            get(path).apply {
                params.forEach { (key, value) -> param(key, value) }
            }

        return mockMvc.perform(request)
    }

    private fun assertAdminGetAllAnimalsSuccess(
        params: Map<String, String> = emptyMap(),
        expectedAnimals: List<AnimalPreview>,
    ) {
        val page = 0
        val size = 10
        val totalElements = expectedAnimals.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedAnimals,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            animalService.getAllAnimals(
                user = any(),
                query = any(),
            )
        } returns expectedResponse

        performGetAllAnimalsRequest(isVet = true, params = params)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    private fun assertUserGetAllAnimalsSuccess(
        userId: Long,
        params: Map<String, String> = emptyMap(),
        expectedAnimals: List<AnimalPreview>,
    ) {
        val page = 0
        val size = 10
        val totalElements = expectedAnimals.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedAnimals,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            animalService.getAllAnimals(
                user = any(),
                query = any(),
            )
        } returns expectedResponse

        performGetAllAnimalsRequest(isVet = false, userId = userId, params = params)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    @Nested
    inner class GetAllAnimalsTests {
        @Nested
        inner class AdminTests {
            @Test
            fun `should return 200 if animals found with name filter for admin`() {
                val expectedAnimals = animals.filter { it.name.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
                assertAdminGetAllAnimalsSuccess(expectedAnimals = expectedAnimals, params = mapOf("name" to "Dog"))
            }

            @Test
            fun `should return 200 if animals found with birthDate filter for admin`() {
                val birthDate = daysAgo(2).toString()
                val expectedAnimals =
                    animals
                        .filter {
                            it.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) ?: false
                        }.map { it.asPreview() }
                assertAdminGetAllAnimalsSuccess(expectedAnimals = expectedAnimals, params = mapOf("birthDate" to birthDate))
            }

            @Test
            fun `should return 200 if animals found with sort direction ASC for admin`() {
                val expectedAnimals = animals.sortedBy { it.name }.map { it.asPreview() }
                assertAdminGetAllAnimalsSuccess(expectedAnimals = expectedAnimals, params = mapOf("sortDirection" to "ASC"))
            }

            @Test
            fun `should return 200 if animals found for admin`() {
                val expectedAnimals = animals.map { it.asPreview() }
                assertAdminGetAllAnimalsSuccess(expectedAnimals = expectedAnimals)
            }
        }

        @Nested
        inner class UserTests {
            @Test
            fun `should return 200 if animals found for user`() {
                val expectedAnimals = animals.map { it.asPreview() }
                assertUserGetAllAnimalsSuccess(userId = validUserId, expectedAnimals = expectedAnimals)
            }

            @Test
            fun `should return 200 if animals found with name filter for user`() {
                val expectedAnimals = animals.filter { it.name.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
                assertUserGetAllAnimalsSuccess(userId = validUserId, expectedAnimals = expectedAnimals, params = mapOf("name" to "Dog"))
            }

            @Test
            fun `should return 200 if animals found with birthDate filter for user`() {
                val birthDate = daysAgo(2).toString()
                val expectedAnimals =
                    animals
                        .filter {
                            it.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) ?: false
                        }.map { it.asPreview() }
                assertUserGetAllAnimalsSuccess(
                    userId = validUserId,
                    expectedAnimals = expectedAnimals,
                    params = mapOf("birthDate" to birthDate),
                )
            }

            @Test
            fun `should return 200 if animals found with sort direction ASC for user`() {
                val expectedAnimals = animals.sortedBy { it.name }.map { it.asPreview() }
                assertUserGetAllAnimalsSuccess(
                    userId = validUserId,
                    expectedAnimals = expectedAnimals,
                    params = mapOf("sortDirection" to "ASC"),
                )
            }
        }
    }

    @Nested
    inner class GetAnimalTests {
        @Test
        fun `should return 400 if animalId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Animals.GET, invalidAnimalId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("animalId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if animal not found on GET`() {
            every {
                animalService.getAnimal(
                    animalId = missingAnimalId,
                )
            } throws ResourceNotFoundException(ResourceType.ANIMAL, missingAnimalId)

            mockMvc
                .perform(
                    get(Path.Animals.GET, missingAnimalId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Animal with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if animal found on GET`() {
            val expectedAnimal = animals.first { it.id == validAnimalId }

            every {
                animalService.getAnimal(
                    animalId = validAnimalId,
                )
            } returns expectedAnimal.asPublic()

            mockMvc
                .perform(
                    get(Path.Animals.GET, validAnimalId),
                ).andExpectSuccessResponse<AnimalInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedAnimal.asPublic(),
                )
        }
    }

    @Nested
    inner class CreateAnimalTests {
        @Test
        fun `should return 409 if animal microchip exists on CREATE`() {
            val expectedAnimal = animals.first { it.id == validAnimalId }
            createAnimalFrom(expectedAnimal)
            val newAnimal = AnimalCreateInputModel(
                name = "Dawg",
                microchip = expectedAnimal.microchip,
            )

            val jsonPart =
                MockMultipartFile(
                    "animal",
                    "animal.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(newAnimal),
                )

            val imagePart =
                MockMultipartFile(
                    "image",
                    "dog.jpg",
                    "image/jpeg",
                    byteArrayOf(1, 2, 3, 4),
                )
            every {
                animalService.createAnimal(
                    createdAnimal = any(),
                    image = any(),
                )
            } throws ResourceAlreadyExistsException(ResourceType.ANIMAL, "microchip", expectedAnimal.microchip ?: "")

            mockMvc
                .perform(
                    multipart(Path.Animals.CREATE)
                        .file(jsonPart)
                        .file(imagePart),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.CONFLICT,
                    expectedMessage = "Resource already exists: Animal with microchip ${expectedAnimal.microchip} already exists",
                    expectedErrorDetails = listOf(null to "Resource already exists"),
                )
        }

        @Test
        fun `should return 200 if animal created successfully`() {
            val expectedAnimal = animals.first()
            val createdAnimal = createAnimalFrom(expectedAnimal)

            val jsonPart =
                MockMultipartFile(
                    "animal",
                    "animal.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(createdAnimal),
                )

            every {
                animalService.createAnimal(
                    createdAnimal = any(),
                    image = any(),
                )
            } returns expectedAnimal.id

            mockMvc
                .perform(
                    multipart(Path.Animals.CREATE)
                        .file(jsonPart)
                ).andExpectSuccessResponse<Map<String, Long>>(
                    expectedStatus = HttpStatus.CREATED,
                    expectedMessage = null,
                    expectedData = mapOf("id" to expectedAnimal.id),
                )
        }
    }

    @Nested
    inner class UpdateAnimalTests {
        @Test
        fun `should return 400 if animalId is invalid on UPDATE`() {
            val updatedAnimal =
                AnimalUpdateInputModel(
                    name = "Dog",
                    microchip = null,
                    birthDate = null,
                    sex = null,
                    sterilized = null,
                    species = null,
                    ownerEmail = null,
                )

            val jsonPart =
                MockMultipartFile(
                    "animal",
                    "animal.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedAnimal),
                )

            val imagePart =
                MockMultipartFile(
                    "image",
                    "dog.jpg",
                    "image/jpeg",
                    byteArrayOf(1, 2, 3, 4),
                )

            mockMvc
                .perform(
                    multipart(Path.Animals.UPDATE, invalidAnimalId)
                        .file(jsonPart)
                        .file(imagePart),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("animalId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if animal not found on UPDATE`() {
            val updatedAnimal =
                AnimalUpdateInputModel(
                    name = "Dog"
                )

            val jsonPart =
                MockMultipartFile(
                    "animal",
                    "animal.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedAnimal),
                )

            val imagePart =
                MockMultipartFile(
                    "image",
                    "dog.jpg",
                    "image/jpeg",
                    byteArrayOf(1, 2, 3, 4),
                )

            every {
                animalService.updateAnimal(
                    id = any(),
                    updatedAnimal = any(),
                    image = any(),
                )
            } throws ResourceNotFoundException(ResourceType.ANIMAL, missingAnimalId)

            mockMvc
                .perform(
                    multipart(Path.Animals.UPDATE, missingAnimalId)
                        .file(jsonPart)
                        .file(imagePart),
                )
                .andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Animal with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if animal updated successfully`() {
            val expectedAnimal = animals.first()
            val updatedAnimal = createAnimalFrom(expectedAnimal)

            every {
                animalService.updateAnimal(
                    id = any(),
                    updatedAnimal = any(),
                    image = any(),
                )
            } returns expectedAnimal.asPublic()

            mockMvc
                .perform(
                    put(Path.Animals.UPDATE, expectedAnimal.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAnimal.toJson()),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    @Nested
    inner class DeleteAnimalTests {
        @Test
        fun `should return 400 if animalId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Animals.DELETE, invalidAnimalId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("animalId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if animal not found on DELETE`() {
            every { animalService.deleteAnimal(missingAnimalId) } throws ResourceNotFoundException(ResourceType.ANIMAL, missingAnimalId)

            mockMvc
                .perform(
                    delete(Path.Animals.DELETE, missingAnimalId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Animal with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if animal deleted successfully`() {
            every { animalService.deleteAnimal(validAnimalId) } returns true

            mockMvc
                .perform(
                    delete(Path.Animals.DELETE, validAnimalId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    private fun createAnimalFrom(animal: Animal): Long =
        animalService.createAnimal(
            createdAnimal = AnimalCreateInputModel(
                name = animal.name,
                microchip = animal.microchip,
                sex = animal.sex,
                sterilized = animal.sterilized,
                species = animal.species,
                birthDate = animal.birthDate,
                ownerEmail = animal.owner?.email,
            ),
            image =
                MockMultipartFile(
                    "file_${animal.id}",
                    "${animal.id}.png",
                    "image/png",
                    ByteArray(10) { 0x1 },
                ),
        )
}
