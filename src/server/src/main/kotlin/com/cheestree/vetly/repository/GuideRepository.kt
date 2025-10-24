package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.guide.Guide
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface GuideRepository :
    JpaRepository<Guide, Long>,
    JpaSpecificationExecutor<Guide> {
    fun existsGuideByTitleAndAuthor_Id(
        title: String,
        authorId: Long,
    ): Boolean
}
