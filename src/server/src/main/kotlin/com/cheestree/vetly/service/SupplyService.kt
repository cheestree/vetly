package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType.CLINIC
import com.cheestree.vetly.domain.exception.VetException.ResourceType.SUPPLY
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinicId
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyAssociateInputModel
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.input.supply.SupplyQueryInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.repository.BaseSpecs.combineAll
import com.cheestree.vetly.repository.MedicalSupplyRepository
import com.cheestree.vetly.repository.clinic.ClinicRepository
import com.cheestree.vetly.repository.supply.SupplyRepository
import com.cheestree.vetly.repository.supply.SupplySpecs
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

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
        query: SupplyQueryInputModel,
    ): ResponseList<MedicalSupplyClinicPreview> {
        val clinic =
            clinicRepository.findById(clinicId).orElseThrow {
                ResourceNotFoundException(CLINIC, clinicId)
            }

        if (!clinic.clinicMemberships.any { it.veterinarian.id == user.id && it.leftIn == null }) {
            throw ForbiddenException("User ${user.id} is not a member of clinic $clinicId")
        }

        val pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
            )

        val specs = combineAll(
            SupplySpecs.byClinicId(clinicId),
            SupplySpecs.byMedicalSupplyClinicName(query.name),
            SupplySpecs.byMedicalSupplyClinicType(query.type),
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

    fun getSupplies(query: SupplyQueryInputModel): ResponseList<MedicalSupplyPreview> {
        val pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val specs = combineAll(
            SupplySpecs.byMedicalSupplyName(query.name),
            SupplySpecs.byMedicalSupplyType(query.type),
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

    fun associateSupplyWithClinic(
        clinicId: Long,
        associateSupply: MedicalSupplyAssociateInputModel,
    ): MedicalSupplyClinicInformation =
        createResource(SUPPLY) {
            val clinic =
                clinicRepository
                    .findById(clinicId)
                    .orElseThrow { ResourceNotFoundException(CLINIC, clinicId) }

            val medicalSupply =
                medicalSupplyRepository
                    .findById(associateSupply.supplyId)
                    .orElseThrow { ResourceNotFoundException(SUPPLY, associateSupply.supplyId) }

            if (supplyRepository.existsByClinicIdAndMedicalSupplyId(clinicId, associateSupply.supplyId)) {
                throw IllegalArgumentException("Supply already associated with clinic")
            }

            val supplyClinic =
                MedicalSupplyClinic(
                    id =
                        MedicalSupplyClinicId(
                            medicalSupply = associateSupply.supplyId,
                            clinic = clinicId,
                        ),
                    clinic = clinic,
                    medicalSupply = medicalSupply,
                    price = associateSupply.price,
                    quantity = associateSupply.quantity,
                )

            supplyRepository.save(supplyClinic).asPublic()
        }

    fun getSupply(supplyId: Long): MedicalSupplyInformation =
        retrieveResource(SUPPLY, supplyId) {
            medicalSupplyRepository
                .findById(supplyId)
                .orElseThrow {
                    ResourceNotFoundException(SUPPLY, supplyId)
                }.asPublic()
        }

    fun updateSupply(
        clinicId: Long,
        supplyId: Long,
        updatedSupply: MedicalSupplyUpdateInputModel,
    ): MedicalSupplyClinicInformation =
        updateResource(SUPPLY, supplyId) {
            val supply =
                supplyRepository.findByClinicIdAndMedicalSupplyId(clinicId, supplyId).orElseThrow {
                    ResourceNotFoundException(SUPPLY, supplyId)
                }

            supply.updateWith(
                quantity = updatedSupply.quantity,
                price = updatedSupply.price,
            )

            supplyRepository.save(supply).asPublic()
        }

    fun deleteSupply(
        clinicId: Long,
        supplyId: Long,
    ): Boolean =
        deleteResource(SUPPLY, supplyId) {
            if (!supplyRepository.existsByClinicIdAndMedicalSupplyId(clinicId, supplyId)) {
                throw ResourceNotFoundException(SUPPLY, supplyId)
            }

            supplyRepository.deleteByClinicIdAndMedicalSupplyId(clinicId, supplyId)
        }
}
