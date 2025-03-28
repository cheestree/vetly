package com.cheestree.vetly.service

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ClinicService(
    private val clinicRepository: ClinicRepository,
    private val userRepository: UserRepository,
){
    private val MAX_PAGE_SIZE = 20

    fun getClinics(
        name: String? = null,
        lat: Double? = null,
        long: Double? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<ClinicPreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, MAX_PAGE_SIZE),
            Sort.by(sortDirection, sortBy.ifBlank { "name" })
        )

        val specs = withFilters<Clinic>(
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%") } },
            { root, cb -> lat?.let { cb.equal(root.get<Double>("lat"), it) } },
            { root, cb -> long?.let { cb.equal(root.get<Double>("long"), it) } }
        )

        return clinicRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getClinic(clinicId: Long): ClinicInformation {
        return clinicRepository.findById(clinicId).orElseThrow {
            ResourceNotFoundException("Clinic $clinicId not found")
        }.asPublic()
    }

    fun createClinic(
        name: String,
        nif: String,
        address: String,
        long: Double,
        lat: Double,
        phone: String,
        email: String,
        imageUrl: String?,
        ownerId: Long
    ): ClinicInformation {
        val owner = userRepository.findById(ownerId).orElseThrow {
            ResourceNotFoundException("User $ownerId not found")
        }

        val clinic = Clinic(
            nif = nif,
            name = name,
            address = address,
            long = long,
            lat = lat,
            phone = phone,
            email = email,
            imageUrl = imageUrl,
            owner = owner,
            veterinarians = setOf(),
        )

        return clinicRepository.save(clinic).asPublic()
    }

    fun updateClinic(
        clinicId: Long,
        name: String? = null,
        nif: String? = null,
        address: String? = null,
        long: Double? = null,
        lat: Double? = null,
        phone: String? = null,
        email: String? = null,
        imageUrl: String? = null,
        ownerId: Long? = null
    ): ClinicInformation {
        val clinic = clinicRepository.findById(clinicId).orElseThrow {
            ResourceNotFoundException("Clinic $clinicId not found")
        }

        val updatedOwner = ownerId?.let {
            userRepository.findById(it).orElseThrow {
                ResourceNotFoundException("User $it not found")
            }
        }

        val updatedClinic = clinic.copy(
            nif = nif ?: clinic.nif,
            name = name ?: clinic.name,
            address = address ?: clinic.address,
            long = long ?: clinic.long,
            lat = lat ?: clinic.lat,
            phone = phone ?: clinic.phone,
            email = email ?: clinic.email,
            imageUrl = imageUrl ?: clinic.imageUrl,
            owner = updatedOwner ?: clinic.owner
        )

        return clinicRepository.save(updatedClinic).asPublic()
    }

    fun deleteClinic(clinicId: Long): Boolean {
        return clinicRepository.deleteClinicById(clinicId)
    }
}