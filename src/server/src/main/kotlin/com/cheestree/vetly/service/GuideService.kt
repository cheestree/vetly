package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class GuideService(
    private val guideRepository: GuideRepository,
    private val userRepository: UserRepository,
    private val appConfig: AppConfig
) {
    fun getAllGuides(
        title: String? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "title",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<GuidePreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<Guide>(
            { root, cb -> title?.let { cb.like(cb.lower(root.get("title")), "%${it.lowercase()}%") } },
        )

        return guideRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getGuide(guideId: Long): GuideInformation {
        return guideRepository.findById(guideId).orElseThrow {
            ResourceNotFoundException("Guide with id $guideId not found")
        }.asPublic()
    }

    fun createGuide(
        veterinarianId: Long,
        title: String,
        description: String,
        imageUrl: String?,
        content: String
    ): Long {
        val veterinarian = userRepository.findVeterinarianById(veterinarianId).orElseThrow {
            ResourceNotFoundException("Veterinarian with id $veterinarianId not found")
        }

        if (guideRepository.existsGuideByTitleAndAuthor_Id(title, veterinarianId)) {
            throw ResourceAlreadyExistsException("Guide with title $title already exists for veterinarian $veterinarianId")
        }

        val guide = Guide(
            title = title,
            description = description,
            imageUrl = imageUrl,
            content = content,
            author = veterinarian
        )

        veterinarian.addGuide(guide)

        return guideRepository.save(guide).id
    }

    fun updateGuide(
        veterinarianId: Long,
        roles: Set<Role>,
        guideId: Long,
        title: String?,
        description: String?,
        imageUrl: String?,
        content: String?
    ): GuideInformation {
        val guide = guideRoleCheck(veterinarianId, roles, guideId)

        guide.updateWith(title, description, imageUrl, content)

        guide.author.updateGuideList(guide)

        return guideRepository.save(guide).asPublic()
    }

    fun deleteGuide(
        veterinarianId: Long,
        roles: Set<Role>,
        guideId: Long
    ): Boolean {
        val guide = guideRoleCheck(veterinarianId, roles, guideId)

        guide.author.removeGuide(guide)

        guideRepository.delete(guide)

        return true
    }

    private fun guideRoleCheck(veterinarianId: Long, roles: Set<Role>, guideId: Long): Guide {
        val guide = guideRepository.findById(guideId).orElseThrow {
            ResourceNotFoundException("Guide with id $guideId not found")
        }

        if(!roles.contains(Role.ADMIN)) {
            if(veterinarianId != guide.author.id) {
                throw UnauthorizedAccessException("Veterinarian with id $veterinarianId is not the author of the guide")
            }
        }

        return guide
    }
}