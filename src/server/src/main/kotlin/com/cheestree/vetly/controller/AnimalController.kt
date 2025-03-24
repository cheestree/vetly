package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.animal.AnimalInputModel
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path.Animals.CREATE
import com.cheestree.vetly.http.path.Path.Animals.DELETE
import com.cheestree.vetly.http.path.Path.Animals.GET
import com.cheestree.vetly.http.path.Path.Animals.GET_ALL
import com.cheestree.vetly.http.path.Path.Animals.UPDATE
import com.cheestree.vetly.service.AnimalService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AnimalController(
    private val animalService: AnimalService
) {
    @GetMapping(GET_ALL)
    @ProtectedRoute(VETERINARIAN)
    fun getAllAnimals(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<List<AnimalPreview>> {
        return ResponseEntity.ok(animalService.getAllAnimals())
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getAnimal(
        authenticatedUser: AuthenticatedUser,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(animalService.getAnimal(animalId))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createAnimal(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid animal: AnimalInputModel
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
        @RequestBody @Valid animal: AnimalInputModel
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
    ): Boolean {
        return animalService.deleteAnimal(animalId)
    }
}