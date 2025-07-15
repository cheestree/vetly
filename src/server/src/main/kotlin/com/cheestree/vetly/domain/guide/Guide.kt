package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.utils.truncateToMillis
import jakarta.persistence.*

@Entity
@Table(name = "guides", schema = "vetly")
class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var description: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "image_file_id")
    var image: File? = null,
    var content: String,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "attachment_file_id")
    var file: File? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    var author: User,
) : BaseEntity() {
    fun updateWith(
        title: String?,
        description: String?,
        image: File?,
        file: File?,
        content: String?,
    ) {
        title?.let { this.title = it }
        description?.let { this.description = it }
        content?.let { this.content = it }
        image?.let { this.image = it }
        file?.let { this.file = it }
    }

    fun asPublic() =
        GuideInformation(
            id = id,
            title = title,
            image = image?.asInformation(),
            description = description,
            content = content,
            file = file?.asInformation(),
            author = author.asPreview(),
            createdAt = createdAt.truncateToMillis(),
            updatedAt = updatedAt.truncateToMillis(),
        )

    fun asPreview() =
        GuidePreview(
            id = id,
            title = title,
            description = description,
            image = image?.asPreview(),
            author = author.asPreview(),
            createdAt = createdAt.truncateToMillis(),
            updatedAt = updatedAt.truncateToMillis(),
        )
}
