package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.filter.Filter
import com.cheestree.vetly.domain.filter.Operation
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideQueryInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.repository.GuideRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.mappedFilters
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class GuideService(
    private val guideRepository: GuideRepository,
    private val userRepository: UserRepository,
    private val firebaseStorageService: FirebaseStorageService,
    private val appConfig: AppConfig,
) {
    fun getAllGuides(query: GuideQueryInputModel): ResponseList<GuidePreview> {
        val pageable: Pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val specs =
            mappedFilters<Guide>(
                listOf(
                    Filter("title", query.title, Operation.LIKE),
                ),
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

    fun getGuide(guideId: Long): GuideInformation =
        retrieveResource(ResourceType.GUIDE, guideId) {
            guideRepository
                .findById(guideId)
                .orElseThrow {
                    ResourceNotFoundException(ResourceType.GUIDE, guideId)
                }.asPublic()
        }

    fun createGuide(
        user: AuthenticatedUser,
        createdGuide: GuideCreateInputModel,
        image: MultipartFile? = null,
        file: MultipartFile? = null,
    ): Long =
        createResource(ResourceType.GUIDE) {
            val veterinarian =
                userRepository.findVeterinarianById(user.id).orElseThrow {
                    ResourceNotFoundException(ResourceType.VETERINARIAN, user.id)
                }

            if (guideRepository.existsGuideByTitleAndAuthor_Id(createdGuide.title, user.id)) {
                throw ResourceAlreadyExistsException(
                    ResourceType.GUIDE,
                    "title + authorId",
                    "title='${createdGuide.title}', authorId=${user.id}",
                )
            }

            val guide =
                Guide(
                    title = createdGuide.title,
                    description = createdGuide.description,
                    imageUrl = null,
                    fileUrl = null,
                    content = createdGuide.content,
                    author = veterinarian,
                )
            veterinarian.addGuide(guide)

            val savedGuide = guideRepository.save(guide)

            val imageUrl =
                image?.let {
                    firebaseStorageService.uploadFile(
                        file = it,
                        folder = StorageFolder.GUIDES,
                        identifier = "${savedGuide.id}",
                        customFileName = "guide_${savedGuide.id}",
                    )
                }
            val fileUrl =
                file?.let {
                    firebaseStorageService.uploadFile(
                        file = it,
                        folder = StorageFolder.GUIDES,
                        identifier = "${savedGuide.id}",
                        customFileName = "guide_${savedGuide.id}_file",
                    )
                }

            savedGuide.imageUrl = imageUrl
            savedGuide.fileUrl = fileUrl
            guideRepository.save(savedGuide).id
        }

    fun updateGuide(
        user: AuthenticatedUser,
        guideId: Long,
        updatedGuide: GuideUpdateInputModel,
        image: MultipartFile?,
        file: MultipartFile?,
    ): GuideInformation {
        val guide = guideRoleCheck(user, guideId)

        val imageUrl =
            image?.let {
                firebaseStorageService.replaceFile(
                    oldFileUrl = guide.imageUrl,
                    newFile = image,
                    folder = StorageFolder.GUIDES,
                    identifier = "temp_${System.currentTimeMillis()}",
                    customFileName = "profile",
                )
            }

        val fileUrl =
            file?.let {
                firebaseStorageService.replaceFile(
                    oldFileUrl = guide.fileUrl,
                    newFile = file,
                    folder = StorageFolder.GUIDES,
                    identifier = "temp_${System.currentTimeMillis()}",
                    customFileName = "guide_file",
                )
            }

        guide.updateWith(updatedGuide.title, updatedGuide.description, imageUrl, fileUrl, updatedGuide.content)

        guide.author.addGuide(guide)

        return guideRepository.save(guide).asPublic()
    }

    fun deleteGuide(
        user: AuthenticatedUser,
        guideId: Long,
    ): Boolean =
        deleteResource(ResourceType.GUIDE, guideId) {
            val guide = guideRoleCheck(user, guideId)

            guide.imageUrl?.let {
                firebaseStorageService.deleteFile(it)
            }

            guide.author.removeGuide(guide)

            guideRepository.delete(guide)

            true
        }

    private fun guideRoleCheck(
        user: AuthenticatedUser,
        guideId: Long,
    ): Guide {
        val guide =
            guideRepository.findById(guideId).orElseThrow {
                ResourceNotFoundException(ResourceType.GUIDE, guideId)
            }

        if (!user.roles.contains(Role.ADMIN) && user.id != guide.author.id) {
            throw UnauthorizedAccessException("Veterinarian with id ${user.id} is not the author of the guide")
        }

        return guide
    }
}
