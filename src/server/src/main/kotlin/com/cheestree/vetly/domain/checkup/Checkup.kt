package com.cheestree.vetly.domain.checkup

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.pet.animal.Animal
import com.cheestree.vetly.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "checkup")
data class Checkup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val description: String,
    val dateTime: OffsetDateTime,
    val missed: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    val animal: Animal,

    @ManyToOne
    @JoinColumn(name = "vet_id", referencedColumnName = "id")
    val veterinarian: User,

    @ManyToOne
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    val clinic: Clinic,

    )