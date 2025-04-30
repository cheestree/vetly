package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.GET_USER_ANIMALS
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Animal")
interface AnimalApi {
    @Operation(
        summary = "Fetches all animals by filters",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_ALL)
    @ProtectedRoute(VETERINARIAN)
    fun getAllAnimals(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "microchip", required = false) microchip: String?,
        @RequestParam(name = "birthDate", required = false) birthDate: String?,
        @RequestParam(name = "species", required = false) species: String?,
        @RequestParam(name = "owned", required = false) owned: Boolean?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>>

    @Operation(
        summary = "Fetches all users' animals by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_USER_ANIMALS)
    @AuthenticatedRoute
    fun getUserAnimals(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "microchip", required = false) microchip: String?,
        @RequestParam(name = "birthDate", required = false) birthDate: String?,
        @RequestParam(name = "species", required = false) species: String?,
        @RequestParam(name = "owned", required = false) owned: Boolean?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>>

    @Operation(
        summary = "Fetches a user's animal by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET)
    @AuthenticatedRoute
    fun getAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
    ): ResponseEntity<AnimalInformation>

    @Operation(
        summary = "Creates a new animal",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid animal: AnimalCreateInputModel,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates an existing animal",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
        @RequestBody @Valid animal: AnimalUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes an animal (deactivates it)",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteAnimal(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
    ): ResponseEntity<Void>
}
