package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.filter.Filter
import com.cheestree.vetly.domain.filter.Operation
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.CheckupRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.checkupOwnershipFilter
import com.cheestree.vetly.service.Utils.Companion.combineAll
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.mappedFilters
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneOffset

@Service
class CheckupService(
    private val checkupRepository: CheckupRepository,
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val clinicRepository: ClinicRepository,
    private val firebaseStorageService: FirebaseStorageService,
    private val appConfig: AppConfig,
) {
    fun getAllCheckups(
        user: AuthenticatedUser,
        query: CheckupQueryInputModel = CheckupQueryInputModel(),
    ): ResponseList<CheckupPreview> {
        val pageable: Pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val ownershipSpec = checkupOwnershipFilter(user.roles, user.id)

        val baseFilters =
            mappedFilters<Checkup>(
                listOf(
                    Filter("title", query.title, Operation.LIKE),
                    Filter(
                        "createdAt",
                        Pair(
                            query.dateTimeStart?.atStartOfDay(ZoneOffset.UTC)?.toOffsetDateTime(),
                            query.dateTimeEnd?.atStartOfDay(ZoneOffset.UTC)?.toOffsetDateTime(),
                        ),
                        Operation.BETWEEN,
                    ),
                    Filter("veterinarian.id", query.veterinarianId, Operation.LIKE),
                    Filter("veterinarian.username", query.veterinarianName, Operation.LIKE),
                    Filter("animal.id", query.animalId, Operation.LIKE),
                    Filter("animal.name", query.animalName, Operation.LIKE),
                    Filter("clinic.id", query.clinicId, Operation.LIKE),
                    Filter("clinic.name", query.clinicName, Operation.LIKE),
                ),
            )

        val finalSpec = combineAll(ownershipSpec, baseFilters)

        val pageResult = checkupRepository.findAll(finalSpec, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getCheckup(
        user: AuthenticatedUser,
        checkupId: Long,
    ): CheckupInformation =
        retrieveResource(ResourceType.CHECKUP, checkupId) {
            val checkup =
                checkupRepository.findById(checkupId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, checkupId)
                }

            if (checkup.animal.owner?.id != user.id && checkup.veterinarian.id != user.id && !user.roles.contains(Role.ADMIN)) {
                throw UnauthorizedAccessException("User ${user.id} does not have access to checkup $checkupId")
            }

            checkup.asPublic()
        }

    fun createCheckUp(
        user: AuthenticatedUser,
        createdCheckup: CheckupCreateInputModel,
        files: List<MultipartFile>?,
    ): Long =
        createResource(ResourceType.CHECKUP) {
            val id = createdCheckup.veterinarianId ?: user.publicId
            val animal =
                animalRepository.findById(createdCheckup.animalId).orElseThrow {
                    ResourceNotFoundException(ResourceType.ANIMAL, createdCheckup.animalId)
                }

            if (!animal.isActive) {
                throw UnauthorizedAccessException("Animal with ID ${animal.id} is not active")
            }

            val veterinarian =
                userRepository.findByPublicId(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.USER, id)
                }

            val clinic =
                clinicRepository.findById(createdCheckup.clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, createdCheckup.clinicId)
                }

            val checkup =
                Checkup(
                    title = createdCheckup.title,
                    description = createdCheckup.description,
                    dateTime = createdCheckup.dateTime,
                    clinic = clinic,
                    veterinarian = veterinarian,
                    animal = animal,
                )
            val savedCheckup = checkupRepository.save(checkup)

            files?.takeIf { it.isNotEmpty() }?.let { fileList ->
                val uploadedFiles =
                    firebaseStorageService.uploadCheckupFiles(
                        fileList.map { file ->
                            StoredFileInputModel(
                                title = file.originalFilename ?: "File",
                                description = null,
                                file = file,
                            )
                        },
                        savedCheckup.id,
                    )
                val uploadedUrls = uploadedFiles.map { it.second }
                savedCheckup.files = uploadedUrls
            }

            val finalCheckup = checkupRepository.save(savedCheckup)
            finalCheckup.id
        }

    fun updateCheckUp(
        user: AuthenticatedUser,
        checkupId: Long,
        updatedCheckup: CheckupUpdateInputModel,
        filesToAdd: List<MultipartFile>? = null,
        filesToRemove: List<String>? = null,
    ): Long =
        updateResource(ResourceType.CHECKUP, checkupId) {
            val checkup =
                checkupRepository.findById(checkupId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, checkupId)
                }

            if (checkup.veterinarian.id != user.id) {
                throw UnauthorizedAccessException("Not authorized to update check-up $checkupId")
            }

            // Convert MultipartFile list into StoredFileInputModel list for upload
            val filesToUpload =
                filesToAdd?.map { file ->
                    StoredFileInputModel(
                        title = file.originalFilename ?: "File",
                        description = null,
                        file = file,
                    )
                }

            val addedUrls =
                filesToUpload?.let { inputs ->
                    firebaseStorageService.uploadCheckupFiles(inputs, checkup.id).map { it.second }
                }

            filesToRemove?.forEach { url ->
                firebaseStorageService.deleteFile(url)
            }

            checkup.updateWith(
                title = updatedCheckup.title,
                dateTime = updatedCheckup.dateTime,
                description = updatedCheckup.description,
                filesToAdd = addedUrls,
                fileUrlsToRemove = filesToRemove,
            )

            checkupRepository.save(checkup).id
        }

    fun deleteCheckup(
        user: AuthenticatedUser,
        checkupId: Long,
    ): Boolean =
        deleteResource(ResourceType.CHECKUP, checkupId) {
            val checkup =
                checkupRepository.findById(checkupId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, checkupId)
                }

            if (!user.roles.contains(Role.ADMIN) && checkup.veterinarian.id != user.id) {
                throw UnauthorizedAccessException("Cannot delete check-up $checkupId")
            }

            firebaseStorageService.deleteFiles(checkup.files)
            checkupRepository.delete(checkup)

            true
        }
}
