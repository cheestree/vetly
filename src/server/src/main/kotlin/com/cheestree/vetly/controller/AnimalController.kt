package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import com.cheestree.vetly.service.AnimalService
import org.springframework.data.domain.Sort
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

@RestController
class AnimalController(
    private val animalService: AnimalService
) {
    @GetMapping(GET_ALL)
    @ProtectedRoute(VETERINARIAN)
    fun getAllAnimals(
        authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "chip", required = false) chip: String?,
        @RequestParam(name = "birth", required = false) birth: String?,
        @RequestParam(name = "breed", required = false) breed: String?,
        @RequestParam(name = "owned", required = false) owned: Boolean?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: String
    ): ResponseEntity<Page<AnimalPreview>> {
        return ResponseEntity.ok(animalService.getAllAnimals(
            name,
            chip,
            birth?.let { runCatching { OffsetDateTime.parse(it) }.getOrNull() },
            breed,
            owned,
            page,
            size,
            sortBy,
            runCatching { Sort.Direction.valueOf(sortDirection.uppercase()) }.getOrDefault(Sort.Direction.DESC)
        ))
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(animalService.getAnimal(
            animalId
        ))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createAnimal(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid animal: AnimalCreateInputModel
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(animalService.createAnimal(
            authenticatedUser,
            animal.name,
            animal.microchip,
            animal.birth,
            animal.breed,
            animal.imageUrl
        ))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long,
        @RequestBody @Valid animal: AnimalUpdateInputModel
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(animalService.updateAnimal(
            animalId,
            animal.name,
            animal.microchip,
            animal.birth,
            animal.breed,
            animal.imageUrl
        ))
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(animalService.deleteAnimal(
            animalId
        ))
    }
}