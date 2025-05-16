package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class GuideService(
    private val guideRepository: GuideRepository,
    private val userRepository: UserRepository,
    private val appConfig: AppConfig,
) {
    fun getAllGuides(
        title: String? = null,
        dateTimeStart: LocalDate? = null,
        dateTimeEnd: LocalDate? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "title",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<GuidePreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val zoneOffset = OffsetDateTime.now().offset

        val specs =
            withFilters<Guide>(
                { root, cb -> title?.let { cb.like(cb.lower(root.get("title")), "%${it.lowercase()}%") } },
                { root, cb ->
                    dateTimeStart?.let {
                        cb.greaterThanOrEqualTo(
                            root.get("createdAt"),
                            it.atStartOfDay().atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES),
                        )
                    }
                },
                { root, cb ->
                    dateTimeEnd?.let {
                        cb.lessThanOrEqualTo(
                            root.get("createdAt"),
                            it.atTime(LocalTime.MAX).atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES),
                        )
                    }
                },
            )

        val pageResult = guideRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getGuide(guideId: Long): GuideInformation {
        return retrieveResource(ResourceType.GUIDE, guideId) {
            guideRepository.findById(guideId).orElseThrow {
                ResourceNotFoundException(ResourceType.GUIDE, guideId)
            }.asPublic()
        }
    }

    fun createGuide(
        veterinarianId: Long,
        title: String,
        description: String,
        imageUrl: String?,
        content: String,
    ): Long {
        return createResource(ResourceType.GUIDE) {
            val veterinarian =
                userRepository.findVeterinarianById(veterinarianId).orElseThrow {
                    ResourceNotFoundException(ResourceType.VETERINARIAN, veterinarianId)
                }

            if (guideRepository.existsGuideByTitleAndAuthor_Id(title, veterinarianId)) {
                throw ResourceAlreadyExistsException(ResourceType.GUIDE, "title + authorId", "title='$title', authorId=$veterinarianId")
            }

            val guide =
                Guide(
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    content = content,
                    author = veterinarian,
                )

            veterinarian.addGuide(guide)

            guideRepository.save(guide).id
        }
    }

    fun updateGuide(
        veterinarianId: Long,
        roles: Set<Role>,
        guideId: Long,
        title: String?,
        description: String?,
        imageUrl: String?,
        content: String?,
    ): GuideInformation {
        val guide = guideRoleCheck(veterinarianId, roles, guideId)

        guide.updateWith(title, description, imageUrl, content)

        guide.author.addGuide(guide)

        return guideRepository.save(guide).asPublic()
    }

    fun deleteGuide(
        veterinarianId: Long,
        roles: Set<Role>,
        guideId: Long,
    ): Boolean {
        return deleteResource(ResourceType.GUIDE, guideId) {
            val guide = guideRoleCheck(veterinarianId, roles, guideId)

            guide.author.removeGuide(guide)

            guideRepository.delete(guide)

            true
        }
    }

    private fun guideRoleCheck(
        veterinarianId: Long,
        roles: Set<Role>,
        guideId: Long,
    ): Guide {
        val guide =
            guideRepository.findById(guideId).orElseThrow {
                ResourceNotFoundException(ResourceType.GUIDE, guideId)
            }

        if (!roles.contains(Role.ADMIN) && veterinarianId != guide.author.id) {
            throw UnauthorizedAccessException("Veterinarian with id $veterinarianId is not the author of the guide")
        }

        return guide
    }
}
