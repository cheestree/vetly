package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.file.StoredFile
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.repository.*
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class CheckupService(
    private val checkupRepository: CheckupRepository,
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val clinicRepository: ClinicRepository,
    private val storedFileRepository: StoredFileRepository,
    private val appConfig: AppConfig
) {
    fun getAllCheckups(
        veterinarianId: Long? = null,
        veterinarianName: String? = null,
        animalId: Long? = null,
        animalName: String? = null,
        clinicId: Long? = null,
        clinicName: String? = null,
        dateTimeStart: OffsetDateTime? = null,
        dateTimeEnd: OffsetDateTime? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "dateTime",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<CheckupPreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<Checkup>(
            { root, cb -> veterinarianId?.let { cb.equal(root.get<User>("veterinarian").get<Long>("id"), it) } },
            { root, cb -> veterinarianName?.let { cb.like(cb.lower(root.get<User>("veterinarian").get("username")), "%${it.lowercase()}%") } },

            { root, cb -> animalId?.let { cb.equal(root.get<Animal>("animal").get<Long>("id"), it) } },
            { root, cb -> animalName?.let { cb.like(cb.lower(root.get<Animal>("animal").get("name")), "%${it.lowercase()}%") } },

            { root, cb -> clinicId?.let { cb.equal(root.get<Clinic>("clinic").get<Long>("id"), it) } },
            { root, cb -> clinicName?.let { cb.like(cb.lower(root.get<Clinic>("clinic").get("name")), "%${it.lowercase()}%") } },

            { root, cb -> dateTimeStart?.let { cb.greaterThanOrEqualTo(root.get("dateTime"), it) } },
            { root, cb -> dateTimeEnd?.let { cb.lessThanOrEqualTo(root.get("dateTime"), it) } }
        )

        return checkupRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getCheckup(userId: Long, checkupId: Long): CheckupInformation {
        val checkup = checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }

        if (checkup.animal.owner?.id != userId && checkup.veterinarian.id != userId) {
            throw UnauthorizedAccessException("User $userId does not have access to checkup $checkupId")
        }

        return checkup.asPublic()
    }

    fun createCheckUp(
        animalId: Long,
        veterinarianId: Long,
        clinicId: Long,
        time: OffsetDateTime,
        description: String,
        files: List<StoredFileInputModel>
    ): Long {
        val animal = animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal $animalId not found")
        }

        val veterinarian = userRepository.findById(veterinarianId).orElseThrow {
            ResourceNotFoundException("Veterinarian $veterinarianId not found")
        }

        val clinic = clinicRepository.findById(clinicId).orElseThrow {
            ResourceNotFoundException("Clinic $clinicId not found")
        }

        val checkup = Checkup(
            description = description,
            dateTime = time,
            clinic = clinic,
            veterinarian = veterinarian,
            animal = animal
        )

        val storedFiles = files.map {
            StoredFile(
                checkup = checkup,
                url = it.url,
                description = it.description
            )
        }

        storedFileRepository.saveAll(storedFiles)

        return checkupRepository.save(checkup).id
    }

    fun updateCheckUp(
        veterinarianId: Long,
        checkupId: Long,
        updatedVetId: Long? = null,
        updatedTime: OffsetDateTime? = null,
        updatedDescription: String? = null,
    ): Long {
        val checkup = checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }

        if (checkup.veterinarian.id != veterinarianId) {
            throw UnauthorizedAccessException("Cannot update check-up $checkupId")
        }

        val updatedVeterinarian = updatedVetId?.let {
            userRepository.findById(it).orElseThrow {
                ResourceNotFoundException("Veterinarian $it not found")
            }
        }

        val updatedCheckup = checkup.copy(
            veterinarian = updatedVeterinarian ?: checkup.veterinarian,
            dateTime = updatedTime ?: checkup.dateTime,
            description = updatedDescription ?: checkup.description
        )

        return checkupRepository.save(updatedCheckup).id
    }

    fun deleteCheckup(
        role: Set<Role>,
        veterinarianId: Long,
        checkupId: Long,
    ): Boolean {
        val checkup = checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }

        if(!role.contains(Role.ADMIN) && checkup.veterinarian.id != veterinarianId) {
            throw UnauthorizedAccessException("Cannot delete check-up $checkupId")
        }

        checkupRepository.delete(checkup)
        return true
    }
}