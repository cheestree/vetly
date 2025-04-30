package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.clinic.ClinicMembershipId
import com.cheestree.vetly.domain.exception.VetException.BadRequestException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.repository.ClinicMembershipRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ClinicService(
    private val clinicRepository: ClinicRepository,
    private val clinicMembershipRepository: ClinicMembershipRepository,
    private val userRepository: UserRepository,
    private val appConfig: AppConfig,
) {
    fun getAllClinics(
        name: String? = null,
        lat: Double? = null,
        lng: Double? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<ClinicPreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val specs =
            withFilters<Clinic>(
                { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%") } },
                { root, cb -> lat?.let { cb.equal(root.get<Double>("latitude"), it) } },
                { root, cb -> lng?.let { cb.equal(root.get<Double>("longitude"), it) } },
            )

        val pageResult = clinicRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
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
        lng: Double,
        lat: Double,
        phone: String,
        email: String,
        imageUrl: String?,
        ownerId: Long?,
    ): Long {
        val owner =
            ownerId?.let {
                userRepository.findById(it).orElseThrow {
                    ResourceNotFoundException("User $it not found")
                }
            }

        if (clinicRepository.existsByNif((nif))) {
            throw ResourceNotFoundException("Clinic with NIF $nif already exists")
        }

        val clinic =
            Clinic(
                nif = nif,
                name = name,
                address = address,
                longitude = lng,
                latitude = lat,
                phone = phone,
                email = email,
                imageUrl = imageUrl,
                owner = owner,
                clinicMemberships = mutableSetOf(),
            )

        return clinicRepository.save(clinic).id
    }

    fun updateClinic(
        clinicId: Long,
        name: String? = null,
        nif: String? = null,
        address: String? = null,
        lng: Double? = null,
        lat: Double? = null,
        phone: String? = null,
        email: String? = null,
        imageUrl: String? = null,
        ownerId: Long? = null,
    ): Long {
        val clinic =
            clinicRepository.findById(clinicId).orElseThrow {
                ResourceNotFoundException("Clinic $clinicId not found")
            }

        val updatedOwner =
            ownerId?.let {
                userRepository.findById(it).orElseThrow {
                    ResourceNotFoundException("User $it not found")
                }
            }

        clinic.updateWith(
            nif = nif,
            name = name,
            address = address,
            lng = lng,
            lat = lat,
            phone = phone,
            email = email,
            imageUrl = imageUrl,
            owner = updatedOwner,
        )

        return clinicRepository.save(clinic).id
    }

    fun deleteClinic(clinicId: Long): Boolean {
        val clinic =
            clinicRepository.findById(clinicId).orElseThrow {
                ResourceNotFoundException("Clinic $clinicId not found")
            }

        clinicRepository.delete(clinic)
        return true
    }

    fun addClinicMember(
        clinicId: Long,
        userId: Long,
    ): Boolean {
        val membershipId = ClinicMembershipId(clinicId, userId)

        if (clinicMembershipRepository.existsById(membershipId)) {
            throw BadRequestException("User $userId is already a member of clinic $clinicId")
        }

        val user =
            userRepository.findById(userId).orElseThrow {
                ResourceNotFoundException("User $userId not found")
            }

        if (!user.roles.any { it.role.role == Role.VETERINARIAN || it.role.role == Role.ADMIN }) {
            throw BadRequestException("User $userId is not a veterinarian")
        }

        val clinic =
            clinicRepository.findById(clinicId).orElseThrow {
                ResourceNotFoundException("Clinic $clinicId not found")
            }

        val membership =
            ClinicMembership(
                id = membershipId,
                veterinarian = user,
                clinic = clinic,
            )

        clinic.clinicMemberships.add(membership)
        user.clinicMemberships.add(membership)

        clinicMembershipRepository.save(membership)
        return true
    }

    fun removeClinicMember(
        clinicId: Long,
        userId: Long,
    ): Boolean {
        val membershipId = ClinicMembershipId(clinicId, userId)

        val membership =
            clinicMembershipRepository.findById(membershipId).orElseThrow {
                ResourceNotFoundException("User $userId is not a member of clinic $clinicId")
            }

        membership.clinic.clinicMemberships.remove(membership)
        membership.veterinarian.clinicMemberships.remove(membership)

        clinicMembershipRepository.delete(membership)
        return true
    }
}
