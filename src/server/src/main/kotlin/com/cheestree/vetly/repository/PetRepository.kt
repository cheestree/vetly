package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.pet.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PetRepository: JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
}