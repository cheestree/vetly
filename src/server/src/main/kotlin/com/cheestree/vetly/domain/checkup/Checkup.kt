package com.cheestree.vetly.domain.checkup

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.file.StoredFile
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "checkup", schema = "vetly")
class Checkup(
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
    val clinic: Clinic,

    @OneToMany(mappedBy = "checkup", cascade = [CascadeType.ALL], orphanRemoval = true)
    val files: List<StoredFile> = emptyList()

) {
    fun copy(
        description: String = this.description,
        dateTime: OffsetDateTime = this.dateTime,
        missed: Boolean = this.missed,
        animal: Animal = this.animal,
        veterinarian: User = this.veterinarian,
        clinic: Clinic = this.clinic,
        files: List<StoredFile> = this.files
    ) = Checkup(
        this.id, description, dateTime, missed, animal, veterinarian, clinic, files
    )
    fun asPublic() = CheckupInformation(
        id = id,
        description = description,
        dateTime = dateTime.truncateToMillis(),
        missed = missed,
        animal = animal.asPublic(),
        veterinarian = veterinarian.asPreview(),
        clinic = clinic.asPreview(),
        files = files.map { it.asPublic() }
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