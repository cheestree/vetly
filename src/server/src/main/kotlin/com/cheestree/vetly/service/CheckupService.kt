package com.cheestree.vetly.service

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.veterinarian.Veterinarian
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.CheckupRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
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
    private val clinicRepository: ClinicRepository
) {
    private val MAX_PAGE_SIZE = 20

    fun getAllCheckups(
        vetId: Long? = null,
        animalId: Long? = null,
        clinicId: Long? = null,
        dateTimeStart: OffsetDateTime? = null,
        dateTimeEnd: OffsetDateTime? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "dateTime",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<CheckupPreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(MAX_PAGE_SIZE),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<Checkup>(
            { root, cb -> vetId?.let { cb.equal(root.get<Veterinarian>("veterinarian").get<Long>("id"), it) } },
            { root, cb -> animalId?.let { cb.equal(root.get<Animal>("animal").get<Long>("id"), it) } },
            { root, cb -> clinicId?.let { cb.equal(root.get<Clinic>("clinic").get<Long>("id"), it) } },
            { root, cb -> dateTimeStart?.let { cb.greaterThanOrEqualTo(root.get("dateTime"), it) } },
            { root, cb -> dateTimeEnd?.let { cb.lessThanOrEqualTo(root.get("dateTime"), it) } }
        )

        return checkupRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getCheckup(checkupId: Long): CheckupInformation {
        return checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }.asPublic()
    }

    fun createCheckUp(
        ownerId: Long,
        petId: Long,
        vetId: Long,
        clinicId: Long,
        time: OffsetDateTime,
        description: String,
    ): CheckupInformation {
        val animal = animalRepository.findById(petId).orElseThrow {
            ResourceNotFoundException("Animal $petId not found")
        }

        val veterinarian = userRepository.findById(vetId).orElseThrow {
            ResourceNotFoundException("veterinarian $petId not found")
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

        return checkupRepository.save(checkup).asPublic()
    }

    fun updateCheckUp(
        vetId: Long,
        checkupId: Long,
        updatedVetId: Long? = null,
        updatedTime: OffsetDateTime? = null,
        updatedDescription: String? = null,
    ): CheckupInformation {
        val checkup = checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }

        if (checkup.veterinarian.id != vetId) {
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

        return checkupRepository.save(updatedCheckup).asPublic()
    }

    fun deleteCheckup(
        veterinarianId: Long,
        checkupId: Long,
    ): Boolean {
        val checkup = checkupRepository.findById(checkupId).orElseThrow {
            ResourceNotFoundException("Checkup $checkupId not found")
        }

        if(checkup.veterinarian.id != veterinarianId) {
            throw UnauthorizedAccessException("Cannot delete check-up $checkupId")
        }

        return checkupRepository.deleteCheckupById(checkupId)
    }
}