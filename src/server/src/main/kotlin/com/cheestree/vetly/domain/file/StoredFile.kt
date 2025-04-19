package com.cheestree.vetly.domain.file

import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.http.model.output.file.StoredFileInformation
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "checkup_files", schema = "vetly")
open class StoredFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "checkup_id", referencedColumnName = "id")
    var checkup: Checkup,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    var url: String,
    var description: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
){
    fun asPublic() = StoredFileInformation(
        uuid = uuid,
        url = url,
        description = description,
        createdAt = createdAt.truncateToMillis(),
    )
}