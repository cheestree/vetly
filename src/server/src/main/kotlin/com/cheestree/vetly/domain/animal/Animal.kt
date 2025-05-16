package com.cheestree.vetly.domain.animal

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId

@Entity
@Table(name = "animals", schema = "vetly")
@Inheritance(strategy = InheritanceType.JOINED)
class Animal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var name: String,
    @Column(unique = true, nullable = true)
    var microchip: String? = null,
    var species: String? = null,
    @Column(nullable = true)
    var birthDate: OffsetDateTime? = null,
    var imageUrl: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    var owner: User? = null,
    var isActive: Boolean = true,
) : BaseEntity() {
    val age: Int? get() =
        birthDate?.let {
            val now = OffsetDateTime.now(ZoneId.systemDefault())
            Period.between(it.toLocalDate(), now.toLocalDate()).years
        }

    fun updateWith(
        name: String?,
        microchip: String?,
        birthDate: OffsetDateTime?,
        species: String?,
        imageUrl: String?,
        owner: User?,
    ) {
        name?.let { this.name = it }
        birthDate?.let { this.birthDate = it }
        species?.let { this.species = it }
        imageUrl?.let { this.imageUrl = it }
        microchip?.let { this.microchip = it }
        owner?.let { this.owner = it }
    }

    fun asPublic() =
        AnimalInformation(
            id = id,
            name = name,
            microchip = microchip,
            species = species!!,
            birthDate = birthDate?.truncateToMillis(),
            imageUrl = imageUrl,
            age = age,
            owner = owner?.asPreview(),
        )

    fun asPreview() =
        AnimalPreview(
            id = id,
            name = name,
            imageUrl = imageUrl,
        )
}
