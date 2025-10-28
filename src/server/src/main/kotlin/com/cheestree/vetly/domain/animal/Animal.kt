package com.cheestree.vetly.domain.animal

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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
    @OneToOne(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    var image: File? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    var owner: User? = null,
    var isActive: Boolean = true,
) : BaseEntity() {
    val age: Int?
        get() =
            birthDate?.let {
                val now = OffsetDateTime.now(ZoneId.systemDefault())
                Period.between(it.toLocalDate(), now.toLocalDate()).years
            }

    fun updateWith(
        name: String,
        microchip: String?,
        sex: Sex,
        sterilized: Boolean,
        birthDate: OffsetDateTime?,
        species: String?,
        owner: User?,
        image: File? = null,
    ) {
        this.name = name
        this.microchip = microchip
        this.sex = sex
        this.sterilized = sterilized
        this.birthDate = birthDate
        this.species = species
        this.owner = owner
        this.image = image
    }

    fun asPublic() =
        AnimalInformation(
            id = id,
            name = name,
            microchip = microchip,
            sex = sex,
            sterilized = sterilized,
            species = species!!,
            birthDate = birthDate?.truncatedTo(ChronoUnit.MILLIS),
            image = image?.asInformation(),
            age = age,
            owner = owner?.asPreview(),
        )

    fun asPreview() =
        AnimalPreview(
            id = id,
            name = name,
            species = species,
            birthDate = birthDate?.truncatedTo(ChronoUnit.MILLIS),
            image = image?.asInformation(),
            age = age,
            owner = owner?.asPreview(),
        )

    override fun toString(): String = "${this.id}: ${this.name} (${this.species}), owned by: ${this.owner?.email}"
}
