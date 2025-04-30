package com.cheestree.vetly.controller

import com.cheestree.vetly.api.AnimalApi
import com.cheestree.vetly.converter.Parsers.Companion.parseOffsetDateTime
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.AnimalService
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AnimalController(
    private val animalService: AnimalService,
) : AnimalApi {
    override fun getAllAnimals(
        name: String?,
        microchip: String?,
        birthDate: String?,
        species: String?,
        owned: Boolean?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>> {
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
                sortDirection = sortDirection,
            ),
        )
    }

    override fun getUserAnimals(
        authenticatedUser: AuthenticatedUser,
        name: String?,
        microchip: String?,
        birthDate: String?,
        species: String?,
        owned: Boolean?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>> {
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
                sortDirection = sortDirection,
            ),
        )
    }

    override fun getAnimal(
        authenticatedUser: AuthenticatedUser,
        animalId: Long,
    ): ResponseEntity<AnimalInformation> {
        return ResponseEntity.ok(
            animalService.getAnimal(
                animalId = animalId,
            ),
        )
    }

    override fun createAnimal(
        authenticatedUser: AuthenticatedUser,
        animal: AnimalCreateInputModel,
    ): ResponseEntity<Map<String, Long>> {
        val id =
            animalService.createAnimal(
                name = animal.name,
                microchip = animal.microchip,
                birthDate = animal.birthDate,
                species = animal.species,
                imageUrl = animal.imageUrl,
                ownerId = animal.ownerId,
            )
        val location = URI.create("${Path.Animals.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    override fun updateAnimal(
        authenticatedUser: AuthenticatedUser,
        animalId: Long,
        animal: AnimalUpdateInputModel,
    ): ResponseEntity<Void> {
        animalService.updateAnimal(
            id = animalId,
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
            ownerId = animal.ownerId,
        )
        return ResponseEntity.noContent().build()
    }

    override fun deleteAnimal(
        authenticatedUser: AuthenticatedUser,
        animalId: Long,
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(
            id = animalId,
        )
        return ResponseEntity.noContent().build()
    }
}
