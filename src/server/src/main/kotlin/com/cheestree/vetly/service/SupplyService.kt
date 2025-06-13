package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.MedicalSupplyRepository
import com.cheestree.vetly.repository.SupplyRepository
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.service.Utils.Companion.withFilters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SupplyService(
    private val supplyRepository: SupplyRepository,
    private val medicalSupplyRepository: MedicalSupplyRepository,
    private val clinicRepository: ClinicRepository,
    private val appConfig: AppConfig,
) {
    fun getClinicSupplies(
        user: AuthenticatedUser,
        clinicId: Long,
        name: String? = null,
        type: SupplyType? = null,
        page: Int = 0,
        size: Int = appConfig.paging.defaultPageSize,
        sortBy: String = "medicalSupply.name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<MedicalSupplyClinicPreview> {
        val clinic =
            clinicRepository.findById(clinicId).orElseThrow {
                ResourceNotFoundException(ResourceType.CLINIC, clinicId)
            }

        if (!clinic.clinicMemberships.any { it.veterinarian.id == user.id && it.leftIn == null }) {
            throw ForbiddenException("User ${user.id} is not a member of clinic $clinicId")
        }

        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val specs =
            withFilters<MedicalSupplyClinic>(
                { root, cb ->
                    clinicId.let {
                        cb.equal(root.get<Clinic>("clinic").get<String>("id"), it)
                    }
                },
                { root, cb ->
                    name?.let {
                        val medicalSupply = root.get<MedicalSupply>("medicalSupply")
                        cb.like(cb.lower(medicalSupply.get("name")), "%${it.lowercase()}%")
                    }
                },
                { root, cb ->
                    type?.let {
                        val medicalSupply = root.get<MedicalSupply>("medicalSupply")
                        cb.equal(medicalSupply.get<SupplyType>("type"), it)
                    }
                },
            )

        val pageResult = supplyRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getSupplies(
        name: String? = null,
        type: SupplyType? = null,
        page: Int = 0,
        size: Int = appConfig.paging.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<MedicalSupplyPreview> {
        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val specs =
            withFilters<MedicalSupply>(
                { root, cb ->
                    name?.let {
                        cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%")
                    }
                },
                { root, cb ->
                    type?.let {
                        cb.equal(root.get<SupplyType>("type"), it)
                    }
                },
            )

        val pageResult = medicalSupplyRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getSupply(supplyId: Long): MedicalSupplyInformation =
        retrieveResource(ResourceType.SUPPLY, supplyId) {
            supplyRepository
                .findById(supplyId)
                .orElseThrow {
                    ResourceNotFoundException(ResourceType.SUPPLY, supplyId)
                }.medicalSupply
                .asPublic()
        }

    fun updateSupply(
        clinicId: Long,
        supplyId: Long,
        quantity: Int? = null,
        price: BigDecimal? = null,
    ): MedicalSupplyClinicInformation =
        updateResource(ResourceType.SUPPLY, supplyId) {
            val supply =
                supplyRepository.findByClinicIdAndMedicalSupplyId(clinicId, supplyId).orElseThrow {
                    ResourceNotFoundException(ResourceType.SUPPLY, supplyId)
                }

            supply.updateWith(
                quantity = quantity,
                price = price,
            )

            supplyRepository.save(supply).asPublic()
        }

    fun deleteSupply(
        clinicId: Long,
        supplyId: Long,
    ): Boolean =
        deleteResource(ResourceType.SUPPLY, supplyId) {
            if (!supplyRepository.existsByClinicIdAndMedicalSupplyId(clinicId, supplyId)) {
                throw ResourceNotFoundException(ResourceType.SUPPLY, supplyId)
            }

            supplyRepository.deleteByClinicIdAndMedicalSupplyId(clinicId, supplyId)
        }
}
