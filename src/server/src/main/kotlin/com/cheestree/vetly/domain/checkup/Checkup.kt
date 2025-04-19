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
open class Checkup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var description: String,
    var dateTime: OffsetDateTime,
    var missed: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    var animal: Animal,

    @ManyToOne
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    var veterinarian: User,

    @ManyToOne
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    var clinic: Clinic,

    @OneToMany(mappedBy = "checkup", cascade = [CascadeType.ALL], orphanRemoval = true)
    val files: MutableList<StoredFile> = mutableListOf()

) {
    fun updateWith(
        veterinarian: User?,
        dateTime: OffsetDateTime?,
        description: String?
    ) {
        veterinarian?.let { this.veterinarian = it }
        dateTime?.let { this.dateTime = it }
        description?.let { this.description = it }
    }

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