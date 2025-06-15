package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.clinic.ClinicMembershipId
import com.cheestree.vetly.domain.clinic.openinghour.OpeningHour
import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.clinic.OpeningHourInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.repository.ClinicMembershipRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.executeOperation
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.service.Utils.Companion.withFilters
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
        size: Int = appConfig.paging.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<ClinicPreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.paging.maxPageSize),
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

    fun getClinic(clinicId: Long): ClinicInformation =
        retrieveResource(ResourceType.CLINIC, clinicId) {
            clinicRepository
                .findById(clinicId)
                .orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
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
        services: Set<ServiceType>,
        openingHours: List<OpeningHourInputModel>,
        imageUrl: String?,
        ownerId: Long?,
    ): Long =
        createResource(ResourceType.CLINIC) {
            val owner =
                ownerId?.let {
                    userRepository.findById(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
                    }
                }

            if (clinicRepository.existsByNif((nif))) {
                throw ResourceAlreadyExistsException(ResourceType.CLINIC, "NIF", nif)
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
                    services = services,
                    imageUrl = imageUrl,
                    owner = owner,
                    clinicMemberships = mutableSetOf(),
                    openingHours = mutableSetOf(),
                )

            clinicRepository.save(clinic)

            val openingHours =
                openingHours.map {
                    OpeningHour(
                        weekday = it.weekday,
                        opensAt = it.opensAt,
                        closesAt = it.closesAt,
                        clinic = clinic,
                    )
                }

            clinic.openingHours.addAll(openingHours)

            clinicRepository.save(clinic).id
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
    ): Long =
        updateResource(ResourceType.CLINIC, clinicId) {
            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
                }

            val updatedOwner =
                ownerId?.let {
                    userRepository.findById(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
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

            clinicRepository.save(clinic).id
        }

    fun deleteClinic(clinicId: Long): Boolean =
        deleteResource(ResourceType.CLINIC, clinicId) {
            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
                }

            clinicRepository.delete(clinic)
            true
        }

    fun addClinicMember(
        clinicId: Long,
        userId: Long,
    ): Boolean =
        executeOperation("add clinic member to", ResourceType.CLINIC) {
            val membershipId = ClinicMembershipId(clinicId, userId)

            if (clinicMembershipRepository.existsById(membershipId)) {
                throw ResourceAlreadyExistsException(ResourceType.CLINIC_MEMBERSHIP, "id", membershipId)
            }

            val user =
                userRepository.findById(userId).orElseThrow {
                    ResourceNotFoundException(ResourceType.USER, userId)
                }

            if (!user.roles.any { it.role.role == Role.VETERINARIAN || it.role.role == Role.ADMIN }) {
                throw ForbiddenException("User $userId is not a veterinarian")
            }

            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
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
            true
        }

    fun removeClinicMember(
        clinicId: Long,
        userId: Long,
    ): Boolean =
        executeOperation("remove clinic member from", ResourceType.CLINIC) {
            val membershipId = ClinicMembershipId(clinicId, userId)

            val membership =
                clinicMembershipRepository.findById(membershipId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC_MEMBERSHIP, membershipId)
                }

            membership.clinic.clinicMemberships.remove(membership)
            membership.veterinarian.clinicMemberships.remove(membership)

            clinicMembershipRepository.delete(membership)
            true
        }
}
