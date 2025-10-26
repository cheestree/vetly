package com.cheestree.vetly.repository.animal

import com.cheestree.vetly.domain.animal.Animal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AnimalRepository :
    JpaRepository<Animal, Long>,
    JpaSpecificationExecutor<Animal> {
    fun existsAnimalByMicrochip(microchip: String): Boolean
}