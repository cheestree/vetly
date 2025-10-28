package com.cheestree.vetly.domain.file

import com.cheestree.vetly.config.file.FileUrlConfig
import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.http.model.output.file.FileInformation
import com.cheestree.vetly.http.model.output.file.FilePreview
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID
import kotlin.jvm.Transient

@Entity
@Table(name = "files", schema = "vetly")
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(unique = true, nullable = false)
    var rawStoragePath: String,
    @Transient
    private var _fullPath: String? = null,
    val fileName: String,
    val mimeType: String,
    val description: String? = null,
    @ManyToOne
    @JoinColumn(name = "checkup_id")
    var checkup: Checkup? = null,
    @OneToOne
    @JoinColumn(name = "animal_id")
    var animal: Animal? = null,
    @OneToOne
    @JoinColumn(name = "clinic_id")
    var clinic: Clinic? = null,
    @ManyToOne
    @JoinColumn(name = "request_id")
    var request: Request? = null,
) : BaseEntity() {
    val storagePath: String
        get() {
            if (_fullPath == null) {
                _fullPath = FileUrlConfig.buildPublicUrl(rawStoragePath)
            }
            return _fullPath!!
        }

    fun asInformation() =
        FileInformation(
            id = id,
            url = storagePath,
            name = fileName,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun asPreview() =
        FilePreview(
            id = id,
            url = storagePath,
        )
}
