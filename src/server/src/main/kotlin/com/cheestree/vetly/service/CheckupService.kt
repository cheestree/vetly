package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.file.StoredFile
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.CheckupRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.StoredFileRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.specification.GenericSpecifications.Companion.checkupOwnershipFilter
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Service
class CheckupService(
    private val checkupRepository: CheckupRepository,
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val clinicRepository: ClinicRepository,
    private val storedFileRepository: StoredFileRepository,
    private val appConfig: AppConfig,
) {
    fun getAllCheckups(
        authenticatedUser: AuthenticatedUser,
        veterinarianId: Long? = null,
        veterinarianName: String? = null,
        animalId: Long? = null,
        animalName: String? = null,
        clinicId: Long? = null,
        clinicName: String? = null,
        dateTimeStart: LocalDate? = null,
        dateTimeEnd: LocalDate? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "createdAt",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<CheckupPreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val zoneOffset = OffsetDateTime.now().offset

        val ownershipSpec = checkupOwnershipFilter(authenticatedUser.roles, authenticatedUser.id)

        val filterSpec =
            withFilters<Checkup>(
                { root, cb -> veterinarianId?.let { cb.equal(root.get<User>("veterinarian").get<Long>("id"), it) } },
                {
                    root,
                    cb,
                    ->
                    veterinarianName?.let { cb.like(cb.lower(root.get<User>("veterinarian").get("username")), "%${it.lowercase()}%") }
                },
                { root, cb -> animalId?.let { cb.equal(root.get<Animal>("animal").get<Long>("id"), it) } },
                { root, cb -> animalName?.let { cb.like(cb.lower(root.get<Animal>("animal").get("name")), "%${it.lowercase()}%") } },
                { root, cb -> clinicId?.let { cb.equal(root.get<Clinic>("clinic").get<Long>("id"), it) } },
                { root, cb -> clinicName?.let { cb.like(cb.lower(root.get<Clinic>("clinic").get("name")), "%${it.lowercase()}%") } },
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

        val finalSpec =
            listOfNotNull(ownershipSpec, filterSpec)
                .reduceOrNull(Specification<Checkup>::and) ?: Specification.where(null)

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
        animalId: Long,
        veterinarianId: Long,
        clinicId: Long,
        time: OffsetDateTime,
        description: String,
        files: List<StoredFileInputModel>,
    ): Long =
        createResource(ResourceType.CHECKUP) {
            val animal =
                animalRepository.findById(animalId).orElseThrow {
                    ResourceNotFoundException(ResourceType.ANIMAL, animalId)
                }

            if (!animal.isActive) {
                throw UnauthorizedAccessException("Animal with ${animal.id} is not active")
            }

            val veterinarian =
                userRepository.findById(veterinarianId).orElseThrow {
                    ResourceNotFoundException(ResourceType.USER, veterinarianId)
                }

            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
                }

            val checkup =
                Checkup(
                    description = description,
                    dateTime = time,
                    clinic = clinic,
                    veterinarian = veterinarian,
                    animal = animal,
                )

            val storedFiles =
                files.map {
                    StoredFile(
                        checkup = checkup,
                        url = it.url,
                        title = it.title,
                        description = it.description,
                    )
                }

            storedFileRepository.saveAll(storedFiles)
            checkupRepository.save(checkup).id
        }

    fun updateCheckUp(
        veterinarianId: Long,
        checkupId: Long,
        dateTime: OffsetDateTime? = null,
        description: String? = null,
        filesToAdd: List<StoredFileInputModel>? = null,
        filesToRemove: List<Long>? = null,
    ): Long =
        updateResource(ResourceType.CHECKUP, checkupId) {
            val checkup =
                checkupRepository.findById(checkupId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, checkupId)
                }

            if (checkup.veterinarian.id != veterinarianId) {
                throw UnauthorizedAccessException("Cannot update check-up $checkupId")
            }

            val filesToAddEntities =
                filesToAdd?.map {
                    StoredFile(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        checkup = checkup,
                    )
                }

            checkup.updateWith(
                dateTime = dateTime,
                description = description,
                filesToAdd = filesToAddEntities,
                fileIdsToRemove = filesToRemove,
            )

            checkupRepository.save(checkup).id
        }

    fun deleteCheckup(
        role: Set<Role>,
        veterinarianId: Long,
        checkupId: Long,
    ): Boolean =
        deleteResource(ResourceType.CHECKUP, checkupId) {
            val checkup =
                checkupRepository.findById(checkupId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CHECKUP, checkupId)
                }

            if (!role.contains(Role.ADMIN) && checkup.veterinarian.id != veterinarianId) {
                throw UnauthorizedAccessException("Cannot delete check-up $checkupId")
            }

            checkupRepository.delete(checkup)

            true
        }
}
