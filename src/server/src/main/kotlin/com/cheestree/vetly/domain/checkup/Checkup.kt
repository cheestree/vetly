package com.cheestree.vetly.domain.checkup

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "checkup")
data class Checkup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val description: String,
    val dateTime: OffsetDateTime,
    val missed: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    val animal: Animal,

    @ManyToOne
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    val veterinarian: User,

    @ManyToOne
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    val clinic: Clinic
) {
    fun asPublic() = CheckupInformation(
        id = id,
        description = description,
        dateTime = dateTime,
        missed = missed,
        animal = animal.asPublic(),
        veterinarian = veterinarian.asPreview(),
        clinic = clinic.asPreview()
    )
    fun asPreview() = CheckupPreview(
        id = id,
        description = description,
        dateTime = dateTime,
        missed = missed,
        animal = animal.asPreview(),
        veterinarian = veterinarian.asPreview(),
        clinic = clinic.asPreview()
    )
}