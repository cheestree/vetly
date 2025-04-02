package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.guide.Guide
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface GuideRepository: JpaRepository<Guide, Long>, JpaSpecificationExecutor<Guide> {
    fun deleteGuideById(id: Long): Boolean
    fun existsGuideByTitle(title: String): Boolean
    fun existsGuideByTitleAndUser_Id(title: String, userId: Long): Boolean
}