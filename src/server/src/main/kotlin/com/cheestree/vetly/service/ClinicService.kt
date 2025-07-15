package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.clinic.ClinicMembershipId
import com.cheestree.vetly.domain.clinic.openinghour.OpeningHour
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.filter.Filter
import com.cheestree.vetly.domain.filter.Operation
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicQueryInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.repository.ClinicMembershipRepository
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.executeOperation
import com.cheestree.vetly.service.Utils.Companion.mappedFilters
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ClinicService(
    private val clinicRepository: ClinicRepository,
    private val clinicMembershipRepository: ClinicMembershipRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
    private val appConfig: AppConfig,
) {
    fun getAllClinics(query: ClinicQueryInputModel = ClinicQueryInputModel()): ResponseList<ClinicPreview> {
        val pageable: Pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val basicFilters =
            mappedFilters<Clinic>(
                listOf(
                    Filter("name", query.name, Operation.LIKE),
                    Filter("latitude", query.lat, Operation.EQUAL),
                    Filter("longitude", query.lng, Operation.EQUAL),
                ),
            )

        val pageResult = clinicRepository.findAll(basicFilters, pageable).map { it.asPreview() }

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
        createdClinic: ClinicCreateInputModel,
        image: MultipartFile?,
    ): Long =
        createResource(ResourceType.CLINIC) {
            val owner =
                createdClinic.ownerEmail?.let {
                    userRepository.findByEmail(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
                    }
                }

            if (clinicRepository.existsByNif((createdClinic.nif))) {
                throw ResourceAlreadyExistsException(ResourceType.CLINIC, "NIF", createdClinic.nif)
            }

            val clinic =
                Clinic(
                    nif = createdClinic.nif,
                    name = createdClinic.name,
                    address = createdClinic.address,
                    longitude = createdClinic.lng,
                    latitude = createdClinic.lat,
                    phone = createdClinic.phone,
                    email = createdClinic.email,
                    services = createdClinic.services,
                    image = null,
                    owner = owner,
                    clinicMemberships = mutableSetOf(),
                    openingHours = mutableSetOf(),
                )

            clinicRepository.save(clinic)

            val image =
                image?.let {
                    storageService.uploadFile(
                        file = it,
                        folder = StorageFolder.CLINICS,
                        identifier = "temp_${System.currentTimeMillis()}",
                        customFileName = "${clinic.id}_${createdClinic.name}",
                    )
                }

            clinic.image = image

            val openingHoursMapped =
                createdClinic.openingHours
                    .map {
                        OpeningHour(
                            weekday = it.weekday,
                            opensAt = it.opensAt,
                            closesAt = it.closesAt,
                            clinic = clinic,
                        )
                    }.toMutableSet()

            openingHoursMapped.let {
                clinic.openingHours.addAll(it)
            }

            clinicRepository.save(clinic).id
        }

    fun updateClinic(
        clinicId: Long,
        updatedClinic: ClinicUpdateInputModel,
        image: MultipartFile? = null,
    ): Long =
        updateResource(ResourceType.CLINIC, clinicId) {
            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
                }

            val updatedOwner =
                updatedClinic.ownerEmail?.let {
                    userRepository.findByEmail(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
                    }
                }

            val imageUrl =
                image?.let {
                    storageService.replaceFile(
                        oldFile = clinic.image,
                        newFile = image,
                        folder = StorageFolder.CLINICS,
                        identifier = "temp_${System.currentTimeMillis()}",
                        customFileName = "${clinicId}_${clinic.name}",
                    )
                }

            clinic.updateWith(
                nif = updatedClinic.nif,
                name = updatedClinic.name,
                address = updatedClinic.address,
                lng = updatedClinic.lng,
                lat = updatedClinic.lat,
                phone = updatedClinic.phone,
                email = updatedClinic.email,
                image = imageUrl,
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

            clinic.image?.let { storageService.deleteFile(it) }

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

            clinicRepository.save(clinic)
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
