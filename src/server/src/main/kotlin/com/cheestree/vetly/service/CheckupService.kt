package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.exception.VetException.*
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.repository.BaseSpecs.combineAll
import com.cheestree.vetly.repository.FileRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.animal.AnimalRepository
import com.cheestree.vetly.repository.checkup.CheckupRepository
import com.cheestree.vetly.repository.checkup.CheckupSpecs
import com.cheestree.vetly.repository.clinic.ClinicRepository
import com.cheestree.vetly.service.Utils.createResource
import com.cheestree.vetly.service.Utils.deleteResource
import com.cheestree.vetly.service.Utils.retrieveResource
import com.cheestree.vetly.service.Utils.updateResource
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CheckupService(
    private val checkupRepository: CheckupRepository,
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val clinicRepository: ClinicRepository,
    private val storageService: StorageService,
    private val fileRepository: FileRepository,
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

        val specs = combineAll(
            CheckupSpecs.ownCheckup(user.roles, user.id),
            CheckupSpecs.titleContains(query.title),
            CheckupSpecs.createdAt(query.dateTimeStart, query.dateTimeEnd),
            CheckupSpecs.veterinarianEquals(query.veterinarianId),
            CheckupSpecs.veterinarianUsernameEquals(query.veterinarianName),
            CheckupSpecs.animalEquals(query.animalId),
            CheckupSpecs.animalNameEquals(query.animalName),
            CheckupSpecs.clinicEquals(query.clinicId),
            CheckupSpecs.clinicNameEquals(query.clinicName),
        )

        val pageResult = checkupRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    @Cacheable(cacheNames = ["checkups"], key = "#id")
    fun getCheckup(
        user: AuthenticatedUser,
        id: Long,
    ): CheckupInformation =
        retrieveResource(ResourceType.CHECKUP, id) {
            val checkup =
                checkupRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, id)
                }

            if (checkup.animal.owner?.id != user.id && checkup.veterinarian.id != user.id && !user.roles.contains(Role.ADMIN)) {
                throw UnauthorizedAccessException("User ${user.id} does not have access to checkup $id")
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
                    storageService.uploadMultipleFiles(
                        files = fileList,
                        folder = StorageFolder.CHECKUPS,
                    )

                if (uploadedFiles.isNotEmpty()) {
                    fileRepository.saveAll(uploadedFiles)
                }
            }

            savedCheckup.id
        }

    @CachePut(cacheNames = ["checkups"], key = "#id")
    fun updateCheckUp(
        user: AuthenticatedUser,
        id: Long,
        updatedCheckup: CheckupUpdateInputModel,
        filesToAdd: List<MultipartFile>? = null,
        filesToRemove: List<String>? = null,
    ): CheckupInformation =
        updateResource(ResourceType.CHECKUP, id) {
            val checkup =
                checkupRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, id)
                }

            if (checkup.veterinarian.id != user.id) {
                throw UnauthorizedAccessException("Not authorized to update check-up $id")
            }

            filesToRemove?.takeIf { it.isNotEmpty() }?.let { paths ->
                val files = fileRepository.findAllByRawStoragePathIn(paths)
                storageService.deleteFiles(files)
                fileRepository.deleteAll(files)
            }

            filesToAdd?.takeIf { it.isNotEmpty() }?.let { newFiles ->
                val uploadedFiles =
                    storageService.uploadMultipleFiles(
                        files = newFiles,
                        folder = StorageFolder.CHECKUPS,
                    )
                fileRepository.saveAll(uploadedFiles)
            }

            checkup.updateWith(
                title = updatedCheckup.title.orElse(checkup.title),
                dateTime = updatedCheckup.dateTime.orElse(checkup.dateTime),
                description = updatedCheckup.description.orElse(checkup.description),
            )

            checkupRepository.save(checkup).asPublic()
        }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["checkups"], key = "#id"),
            CacheEvict(cacheNames = ["checkupsList"], allEntries = true),
        ]
    )
    fun deleteCheckup(
        user: AuthenticatedUser,
        id: Long,
    ): Boolean =
        deleteResource(ResourceType.CHECKUP, id) {
            val checkup =
                checkupRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, id)
                }

            if (!user.roles.contains(Role.ADMIN) && checkup.veterinarian.id != user.id) {
                throw UnauthorizedAccessException("Cannot delete check-up $id")
            }

            storageService.deleteFiles(checkup.files)
            checkupRepository.delete(checkup)

            true
        }
}
