package com.cheestree.vetly.domain.checkup

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.status.CheckupStatus
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "checkups", schema = "vetly")
class Checkup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var description: String,
    var dateTime: OffsetDateTime,
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "vetly.checkup_status")
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
    @JdbcTypeCode(SqlTypes.ARRAY)
    var files: List<String> = listOf(),
    //  @Lob if it's a lot of text
    var notes: String = "",
) : BaseEntity() {
    fun updateWith(
        dateTime: OffsetDateTime?,
        title: String?,
        description: String?,
        filesToAdd: List<String>?,
        fileUrlsToRemove: List<String>?,
    ) {
        dateTime?.let { this.dateTime = it }
        title?.let { this.title = it }
        description?.let { this.description = it }

        val mutableFiles = this.files.toMutableList()

        fileUrlsToRemove?.let { urlsToRemove ->
            mutableFiles.removeAll(urlsToRemove)
        }

        filesToAdd?.let { newFiles ->
            mutableFiles.addAll(newFiles)
        }

        this.files = mutableFiles.distinct()
    }

    fun asPublic() =
        CheckupInformation(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime.truncateToMillis(),
            status = status,
            animal = animal.asPublic(),
            veterinarian = veterinarian.asLink(),
            clinic = clinic.asLink(),
            files = files,
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
