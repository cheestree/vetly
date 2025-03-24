package com.cheestree.vetly.service

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class GuideService(
    private val guideRepository: GuideRepository
) {
    private val MAX_PAGE_SIZE = 20

    fun getGuides(
        name: String? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<GuidePreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, MAX_PAGE_SIZE),
            Sort.by(sortDirection, sortBy.ifBlank { "name" })
        )

        val specs = withFilters<Guide>(
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%") } },
        )

        return guideRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getGuide(guideId: Long): GuidePreview {
        return guideRepository.findById(guideId).orElseThrow {
            ResourceNotFoundException("Guide with id $guideId not found")
        }.asPreview()
    }

    fun deleteGuide(guideId: Long): Boolean {
        return guideRepository.deleteGuideById(guideId)
    }
}