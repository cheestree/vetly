package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.AnimalApi
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalQueryInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.AnimalService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
class AnimalController(
    private val animalService: AnimalService,
) : AnimalApi {
    @AuthenticatedRoute
    override fun getAllAnimals(
        user: AuthenticatedUser,
        query: AnimalQueryInputModel,
    ): ResponseEntity<ResponseList<AnimalPreview>> = ResponseEntity.ok(animalService.getAllAnimals(user, query))

    @AuthenticatedRoute
    override fun getAnimal(
        user: AuthenticatedUser,
        animalId: Long,
    ): ResponseEntity<AnimalInformation> = ResponseEntity.ok(animalService.getAnimal(animalId))

    @ProtectedRoute(VETERINARIAN)
    override fun createAnimal(
        user: AuthenticatedUser,
        createdAnimal: AnimalCreateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<Map<String, Long>> {
        val id = animalService.createAnimal(createdAnimal, image)
        val location = URI.create("${Path.Animals.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateAnimal(
        user: AuthenticatedUser,
        animalId: Long,
        updatedAnimal: AnimalUpdateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<Void> {
        animalService.updateAnimal(animalId, updatedAnimal, image)

        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteAnimal(
        user: AuthenticatedUser,
        animalId: Long,
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(animalId)
        return ResponseEntity.noContent().build()
    }
}
