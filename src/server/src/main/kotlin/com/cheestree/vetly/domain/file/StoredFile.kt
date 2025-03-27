package com.cheestree.vetly.domain.file

import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.http.model.output.file.StoredFileInformation
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "checkup_files", schema = "vetly")
class StoredFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "checkup_id", referencedColumnName = "id")
    val checkup: Checkup,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    val url: String,
    val description: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
){
    fun asPublic() = StoredFileInformation(
        uuid = uuid,
        url = url,
        description = description,
        createdAt = createdAt
    )
}