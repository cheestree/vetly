package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.veterinarian.Veterinarian
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

    @Column(name = "updated_at", nullable = false)
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    val veterinarian: Veterinarian
){
    fun asPublic() = GuideInformation(
        id = id,
        title = title,
        imageUrl = imageUrl,
        description = description,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        content = text
    )
    fun asPreview() = GuidePreview(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}