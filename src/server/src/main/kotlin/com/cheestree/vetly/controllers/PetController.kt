package com.cheestree.vetly.controllers

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.VETERINARIAN
import com.cheestree.vetly.domain.model.input.animal.AnimalAssignInputModel
import com.cheestree.vetly.domain.model.input.pet.PetInputModel
import com.cheestree.vetly.domain.model.input.pet.PetUpdateInputModel
import com.cheestree.vetly.domain.path.Path.Animals.ASSIGN
import com.cheestree.vetly.domain.path.Path.Animals.UNASSIGN
import com.cheestree.vetly.domain.path.Path.Pets.CREATE
import com.cheestree.vetly.domain.path.Path.Pets.UPDATE
import com.cheestree.vetly.domain.pet.Pet
import com.cheestree.vetly.domain.pet.animal.Animal
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.services.PetService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PetController(
    private val petService: PetService,
) {
    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createPet(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid pet: PetInputModel
    ): ResponseEntity<Animal> {
        return ResponseEntity.ok(petService.createPet(
            authenticatedUser,
            pet.ownerId,
            pet.name,
            pet.microchip,
            pet.birth,
            pet.breed,
            pet.imageUrl
        ))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updatePet(
        authenticatedUser: AuthenticatedUser,
        @PathVariable petId: Long,
        @RequestBody @Valid pet: PetUpdateInputModel
    ): ResponseEntity<Animal> {
        return ResponseEntity.ok(petService.updatePet(
            authenticatedUser,
            petId,
            pet.name,
            pet.birth,
            pet.breed,
            pet.imageUrl,
            pet.ownerId
        ))
    }


    @PostMapping(ASSIGN)
    @ProtectedRoute(VETERINARIAN)
    fun assignAnimalToUser(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid pet: AnimalAssignInputModel
    ): ResponseEntity<Pet> {
        return ResponseEntity.ok(petService.assignAnimalToUser(
            authenticatedUser,
            pet.animalId,
            pet.userId
        ))
    }

    @DeleteMapping(UNASSIGN)
    @ProtectedRoute(VETERINARIAN)
    fun deleteAnimal(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid pet: AnimalAssignInputModel
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(petService.unassignAnimalFromUser(
            authenticatedUser,
            pet.animalId
        ))
    }
}