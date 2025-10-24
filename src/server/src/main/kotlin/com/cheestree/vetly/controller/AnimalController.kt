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
        id: Long,
    ): ResponseEntity<AnimalInformation> = ResponseEntity.ok(animalService.getAnimal(id))

    @ProtectedRoute(VETERINARIAN)
    override fun createAnimal(
        user: AuthenticatedUser,
        createdAnimal: AnimalCreateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<Map<String, Long>> {
        val animal = animalService.createAnimal(createdAnimal, image)
        val location = URI.create("${Path.Animals.BASE}/${animal.id}")

        return ResponseEntity.created(location).body(mapOf("id" to animal.id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateAnimal(
        user: AuthenticatedUser,
        id: Long,
        updatedAnimal: AnimalUpdateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<AnimalInformation> = ResponseEntity.ok(animalService.updateAnimal(id, updatedAnimal, image))

    @ProtectedRoute(VETERINARIAN)
    override fun deleteAnimal(
        user: AuthenticatedUser,
        id: Long,
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(id)
        return ResponseEntity.noContent().build()
    }
}
