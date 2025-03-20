package com.cheestree.vetly.domain.pet

import com.cheestree.vetly.domain.pet.animal.Animal
import com.cheestree.vetly.domain.user.User
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "pet", schema = "vetly")
class Pet(
    name: String?,
    chip: String?,
    birth: OffsetDateTime?,
    breed: String?,
    imageUrl: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    val owner: User
) : Animal(
    name = name,
    chip = chip,
    birth = birth,
    breed = breed,
    imageUrl = imageUrl
) {
    fun copy(
        name: String? = this.name,
        chip: String? = this.chip,
        birth: OffsetDateTime? = this.birth,
        breed: String? = this.breed,
        imageUrl: String? = this.imageUrl,
        owner: User = this.owner
    ) = Pet(name, chip, birth, breed, imageUrl, owner)
}