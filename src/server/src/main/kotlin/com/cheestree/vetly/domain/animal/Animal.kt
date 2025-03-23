package com.cheestree.vetly.domain.animal

import com.cheestree.vetly.domain.user.User
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
    val id: Long? = null,

    @Column(nullable = true)
    val name: String? = null,

    @Column(unique = true, nullable = true)
    val chip: String? = null,

    val breed: String? = null,

    @Column(nullable = true)
    val birth: OffsetDateTime? = null,
    val imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User? = null
) {
    val age: Int? get() = birth?.let {
        val now = OffsetDateTime.now(ZoneId.systemDefault())
        Period.between(it.toLocalDate(), now.toLocalDate()).years
    }

    fun copy(
        name: String? = this.name,
        chip: String? = this.chip,
        breed: String? = this.breed,
        birth: OffsetDateTime? = this.birth,
        imageUrl: String? = this.imageUrl
    ) = Animal(id, name, chip, breed, birth, imageUrl)
}