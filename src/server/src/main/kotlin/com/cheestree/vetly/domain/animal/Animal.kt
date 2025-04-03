package com.cheestree.vetly.domain.animal

import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId

@Entity
@Table(name = "animal", schema = "vetly")
@Inheritance(strategy = InheritanceType.JOINED)
class Animal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true, nullable = true)
    val microchip: String? = null,

    val species: String? = null,

    @Column(nullable = true)
    val birthDate: OffsetDateTime? = null,
    val imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User? = null
) {
    val age: Int? get() = birthDate?.let {
        val now = OffsetDateTime.now(ZoneId.systemDefault())
        Period.between(it.toLocalDate(), now.toLocalDate()).years
    }

    fun copy(
        name: String = this.name,
        microchip: String? = this.microchip,
        species: String? = this.species,
        birthDate: OffsetDateTime? = this.birthDate,
        imageUrl: String? = this.imageUrl
    ) = Animal(id, name, microchip, species, birthDate, imageUrl)

    fun asPublic() = AnimalInformation(
        id = id,
        name = name,
        microchip = microchip,
        species = species!!,
        birthDate = birthDate?.truncateToMillis(),
        imageUrl = imageUrl,
        age = age,
        owner = owner?.asPreview()
    )
    fun asPreview() = AnimalPreview(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}