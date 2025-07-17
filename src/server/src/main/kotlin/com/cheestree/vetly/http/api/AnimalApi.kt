package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalQueryInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE_ANIMAL
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

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
            ),
        ],
    )
    @GetMapping(GET_ALL)
    fun getAllAnimals(
        @HiddenUser user: AuthenticatedUser,
        @ModelAttribute query: AnimalQueryInputModel,
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
            ),
        ],
    )
    @GetMapping(GET)
    fun getAnimal(
        @HiddenUser user: AuthenticatedUser,
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
            ),
        ],
    )
    @PostMapping(CREATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createAnimal(
        @HiddenUser user: AuthenticatedUser,
        @RequestPart("animal") @Valid createdAnimal: AnimalCreateInputModel,
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
        ],
    )
    @PatchMapping(UPDATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateAnimal(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable animalId: Long,
        @RequestPart("animal") @Valid updatedAnimal: AnimalUpdateInputModel,
        @RequestPart("image", required = false) image: MultipartFile? = null,
    ): ResponseEntity<AnimalInformation>

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
        ],
    )
    @DeleteMapping(DELETE_ANIMAL)
    fun deleteAnimal(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable animalId: Long,
    ): ResponseEntity<Void>
}
