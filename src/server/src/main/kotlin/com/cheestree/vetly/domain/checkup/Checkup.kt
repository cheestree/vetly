package com.cheestree.vetly.domain.checkup

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.status.CheckupStatus
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.model.output.file.FileInformation
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "checkups", schema = "vetly")
class Checkup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var description: String,
    var dateTime: OffsetDateTime,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CheckupStatus = CheckupStatus.SCHEDULED,
    @ManyToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    val animal: Animal,
    @ManyToOne
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    val veterinarian: User,
    @ManyToOne
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    val clinic: Clinic,
    @OneToMany(mappedBy = "checkup", cascade = [CascadeType.ALL])
    val files: List<File> = emptyList(),
    //  @Lob if it's a lot of text
    var notes: String = "",
) : BaseEntity() {
    fun updateWith(
        dateTime: OffsetDateTime?,
        title: String?,
        description: String?,
    ) {
        dateTime?.let { this.dateTime = it }
        title?.let { this.title = it }
        description?.let { this.description = it }
    }

    fun asPublic() =
        CheckupInformation(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime.truncatedTo(ChronoUnit.MILLIS),
            status = status,
            animal = animal.asPublic(),
            veterinarian = veterinarian.asLink(),
            clinic = clinic.asLink(),
            files =
                files.map {
                    FileInformation(
                        it.id,
                        it.storagePath,
                        it.fileName,
                        it.description,
                        it.createdAt,
                        it.updatedAt,
                    )
                },
        )

    fun asPreview() =
        CheckupPreview(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime,
            status = status,
            animal = animal.asPreview(),
            veterinarian = veterinarian.asPreview(),
            clinic = clinic.asLink(),
        )
}
