package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "guide", schema = "vetly")
class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val description: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    val text: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "modified_at", nullable = true)
    val modifiedAt: OffsetDateTime? = OffsetDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    val user: User
){
    fun copy(
        title: String = this.title,
        description: String = this.description,
        imageUrl: String? = this.imageUrl,
        text: String = this.text
    ) = Guide(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        text = text,
        createdAt = createdAt,
        modifiedAt = OffsetDateTime.now(),
        user = user
    )

    fun asPublic() = GuideInformation(
        id = id,
        title = title,
        imageUrl = imageUrl,
        description = description,
        content = text,
        createdAt = createdAt.toString(),
        updatedAt = modifiedAt?.toString()
    )
    fun asPreview() = GuidePreview(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt.toString(),
        updatedAt = modifiedAt?.toString()
    )
}