package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "guide", schema = "vetly")
open class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String,
    var description: String,

    @Column(nullable = true)
    var imageUrl: String? = null,

    var content: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "modified_at", nullable = true)
    var modifiedAt: OffsetDateTime? = OffsetDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    var author: User
){
    fun updateWith(
        title: String?,
        description: String?,
        imageUrl: String?,
        content: String?
    ) {
        title?.let { this.title = it }
        description?.let { this.description = it }
        content?.let { this.content = it }
        this.imageUrl = imageUrl
    }

    fun asPublic() = GuideInformation(
        id = id,
        title = title,
        imageUrl = imageUrl,
        description = description,
        content = content,
        createdAt = createdAt.truncateToMillis(),
        updatedAt = modifiedAt?.truncateToMillis()
    )
    fun asPreview() = GuidePreview(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt.truncateToMillis(),
        updatedAt = modifiedAt?.truncateToMillis()
    )
}