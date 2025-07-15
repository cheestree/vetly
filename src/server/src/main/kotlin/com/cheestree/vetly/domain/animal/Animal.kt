package com.cheestree.vetly.domain.animal

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "animals", schema = "vetly")
class Animal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var name: String,
    @Column(unique = true, nullable = true)
    var microchip: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sex: Sex = Sex.UNKNOWN,
    var sterilized: Boolean = false,
    var species: String? = null,
    @Column(nullable = true)
    var birthDate: OffsetDateTime? = null,
    @OneToOne(mappedBy = "animal", cascade = [CascadeType.ALL])
    var image: File? = null,
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
        sex: Sex?,
        sterilized: Boolean?,
        birthDate: OffsetDateTime?,
        species: String?,
        owner: User?,
        imageFile: File? = null
    ) {
        name?.let { this.name = it }
        microchip?.let { this.microchip = it }
        sex?.let { this.sex = it }
        sterilized?.let { this.sterilized = it }
        birthDate?.let { this.birthDate = it }
        species?.let { this.species = it }
        owner?.let { this.owner = it }
        imageFile?.let { this.image = it }
    }

    fun asPublic() =
        AnimalInformation(
            id = id,
            name = name,
            microchip = microchip,
            sex = sex,
            sterilized = sterilized,
            species = species!!,
            birthDate = birthDate?.truncateToMillis(),
            image = image?.asInformation(),
            age = age,
            owner = owner?.asPreview(),
        )

    fun asPreview() =
        AnimalPreview(
            id = id,
            name = name,
            species = species,
            birthDate = birthDate?.truncateToMillis(),
            image = image?.asInformation(),
            age = age,
            owner = owner?.asPreview(),
        )

    override fun toString(): String = "${this.id}: ${this.name} (${this.species}), owned by: ${this.owner?.email}"
}
