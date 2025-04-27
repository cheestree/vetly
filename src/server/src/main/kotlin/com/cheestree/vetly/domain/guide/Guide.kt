package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "guides", schema = "vetly")
open class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var description: String,
    @Column(nullable = true)
    var imageUrl: String? = null,
    var content: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    var author: User,
) : BaseEntity() {
    fun updateWith(
        title: String?,
        description: String?,
        imageUrl: String?,
        content: String?,
    ) {
        title?.let { this.title = it }
        description?.let { this.description = it }
        content?.let { this.content = it }
        this.imageUrl = imageUrl
    }

    fun asPublic() =
        GuideInformation(
            id = id,
            title = title,
            imageUrl = imageUrl,
            description = description,
            content = content,
            createdAt = createdAt.truncateToMillis(),
            updatedAt = updatedAt.truncateToMillis(),
        )

    fun asPreview() =
        GuidePreview(
            id = id,
            title = title,
            description = description,
            imageUrl = imageUrl,
            createdAt = createdAt.truncateToMillis(),
            updatedAt = updatedAt.truncateToMillis(),
        )
}
