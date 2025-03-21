package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.guide.Guide
import org.springframework.data.jpa.repository.JpaRepository

interface GuideRepository: JpaRepository<Guide, Long> {
}