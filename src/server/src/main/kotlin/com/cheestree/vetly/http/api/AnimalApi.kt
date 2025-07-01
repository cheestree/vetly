package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Tag(name = "Animal")
interface AnimalApi {
    @Operation(
        summary = "Fetches all animals by filters",
        description = "Authenticated users can fetch their animals. Veterinarians can fetch all animals.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Animals fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_ALL)
    fun getAllAnimals(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "userId", required = false) userId: Long?,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "microchip", required = false) microchip: String?,
        @RequestParam(name = "sex", required = false) sex: Sex?,
        @RequestParam(name = "sterilized", required = false) sterilized: Boolean?,
        @RequestParam(name = "species", required = false) species: String?,
        @RequestParam(name = "birthDate", required = false) birthDate: LocalDate?,
        @RequestParam(name = "owned", required = false) owned: Boolean?,
        @RequestParam(name = "self", required = false) self: Boolean?,
        @RequestParam(name = "active", required = false) active: Boolean?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>>

    @Operation(
        summary = "Fetches a user's animal by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Animal fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AnimalInformation::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
    ): ResponseEntity<AnimalInformation>

    @Operation(
        summary = "Creates a new animal",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Animal created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(CREATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestPart("animal") @Valid animal: AnimalCreateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates an existing animal",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Animal updated successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(UPDATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
        @RequestPart("animal") @Valid animal: AnimalUpdateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes an animal (deactivates it)",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Animal deleted successfully",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
    ): ResponseEntity<Void>
}
