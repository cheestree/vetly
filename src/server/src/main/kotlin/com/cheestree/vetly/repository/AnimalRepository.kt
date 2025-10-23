package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.animal.Animal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.redis.core.RedisHash
import org.springframework.stereotype.Repository

@Repository
interface AnimalRepository :
    JpaRepository<Animal, Long>,
    JpaSpecificationExecutor<Animal> {
    fun existsAnimalByMicrochip(microchip: String): Boolean
}
