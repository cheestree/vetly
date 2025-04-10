package com.cheestree.vetly.controller

import com.cheestree.vetly.converter.Parsers.Companion.parseOffsetDateTime
import com.cheestree.vetly.converter.Parsers.Companion.parseSortDirection
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.GET_USER_ANIMALS
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import com.cheestree.vetly.service.AnimalService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
class AnimalController(
    private val animalService: AnimalService
) {
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
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<AnimalPreview>> {
        return ResponseEntity.ok(
            animalService.getAllAnimals(
                name = name,
                microchip = microchip,
                birthDate = parseOffsetDateTime(birthDate),
                species = species,
                owned = owned,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET_USER_ANIMALS)
    @AuthenticatedRoute
    fun getUserAnimals(
        authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "microchip", required = false) microchip: String?,
        @RequestParam(name = "birthDate", required = false) birthDate: String?,
        @RequestParam(name = "species", required = false) species: String?,
        @RequestParam(name = "owned", required = false) owned: Boolean?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<AnimalPreview>> {
        return ResponseEntity.ok(
            animalService.getAllAnimals(
                userId = authenticatedUser.id,
                name = name,
                microchip = microchip,
                birthDate = parseOffsetDateTime(birthDate),
                species = species,
                owned = owned,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(
            animalService.getAnimal(
                animalId = animalId
            )
        )
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createAnimal(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid animal: AnimalCreateInputModel
    ): ResponseEntity<Map<String, Long>> {
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl
        )
        val location = URI.create("${Path.Animals.BASE}/${id}")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
        @RequestBody @Valid animal: AnimalUpdateInputModel
    ): ResponseEntity<Void> {
        animalService.updateAnimal(
            id = animalId,
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(
            id = animalId
        )
        return ResponseEntity.noContent().build()
    }
}