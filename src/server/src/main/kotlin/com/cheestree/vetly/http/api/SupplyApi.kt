package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyAssociateInputModel
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.input.supply.SupplyQueryInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.http.path.Path.Supplies.ASSOCIATE_SUPPLY
import com.cheestree.vetly.http.path.Path.Supplies.DELETE
import com.cheestree.vetly.http.path.Path.Supplies.GET_ALL
import com.cheestree.vetly.http.path.Path.Supplies.GET_CLINIC_SUPPLIES
import com.cheestree.vetly.http.path.Path.Supplies.GET_SUPPLY
import com.cheestree.vetly.http.path.Path.Supplies.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Supply")
interface SupplyApi {
    @Operation(
        summary = "Fetches all medical supplies by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Supplies fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_ALL)
    fun getAllSupplies(
        @ModelAttribute query: SupplyQueryInputModel,
    ): ResponseEntity<ResponseList<MedicalSupplyPreview>>

    @Operation(
        summary = "Fetches all clinic supplies by filters",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Clinic supplies fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_CLINIC_SUPPLIES)
    fun getClinicSupplies(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable clinicId: Long,
        @ModelAttribute query: SupplyQueryInputModel,
    ): ResponseEntity<ResponseList<MedicalSupplyClinicPreview>>

    @Operation(
        summary = "Fetches medical supply by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Supply fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MedicalSupplyInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_SUPPLY)
    fun getSupply(
        @PathVariable supplyId: Long,
    ): ResponseEntity<MedicalSupplyInformation>

    @PutMapping(ASSOCIATE_SUPPLY)
    fun associateSupplyWithClinic(
        @PathVariable clinicId: Long,
        @RequestBody @Valid associateSupply: MedicalSupplyAssociateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Updates medical supply by ID",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Supply updated successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Void::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(UPDATE)
    fun updateSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
        @RequestBody @Valid updatedSupply: MedicalSupplyUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes medical supply by ID",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Supply deleted successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Void::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
    ): ResponseEntity<Void>
}
