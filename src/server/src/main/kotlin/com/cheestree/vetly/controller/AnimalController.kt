package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.AnimalApi
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
import java.time.LocalDate

@RestController
class AnimalController(
    private val animalService: AnimalService,
) : AnimalApi {
    @AuthenticatedRoute
    override fun getAllAnimals(
        authenticatedUser: AuthenticatedUser,
        userId: Long?,
        name: String?,
        microchip: String?,
        sex: Sex?,
        sterilized: Boolean?,
        species: String?,
        birthDate: LocalDate?,
        owned: Boolean?,
        self: Boolean?,
        active: Boolean?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<AnimalPreview>> =
        ResponseEntity.ok(
            animalService.getAllAnimals(
                user = authenticatedUser,
                userId = userId,
                name = name,
                microchip = microchip,
                sex = sex,
                sterilized = sterilized,
                species = species,
                birthDate = birthDate,
                owned = owned,
                active = active,
                self = self,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @AuthenticatedRoute
    override fun getAnimal(
        authenticatedUser: AuthenticatedUser,
        animalId: Long,
    ): ResponseEntity<AnimalInformation> =
        ResponseEntity.ok(
            animalService.getAnimal(
                animalId = animalId,
            ),
        )

    @ProtectedRoute(VETERINARIAN)
    override fun createAnimal(
        authenticatedUser: AuthenticatedUser,
        animal: AnimalCreateInputModel,
    ): ResponseEntity<Map<String, Long>> {
        val id =
            animalService.createAnimal(
                name = animal.name,
                microchip = animal.microchip,
                sex = animal.sex,
                sterilized = animal.sterilized,
                species = animal.species,
                birthDate = animal.birthDate,
                imageUrl = animal.imageUrl,
                ownerId = animal.ownerId,
            )
        val location = URI.create("${Path.Animals.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateAnimal(
        authenticatedUser: AuthenticatedUser,
        animalId: Long,
        animal: AnimalUpdateInputModel,
    ): ResponseEntity<Void> {
        animalService.updateAnimal(
            id = animalId,
            name = animal.name,
            microchip = animal.microchip,
            sex = animal.sex,
            sterilized = animal.sterilized,
            species = animal.species,
            birthDate = animal.birthDate,
            imageUrl = animal.imageUrl,
            ownerId = animal.ownerId,
        )
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
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
