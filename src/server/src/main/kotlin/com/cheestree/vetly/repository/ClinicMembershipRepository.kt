package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.clinic.ClinicMembershipId
import org.springframework.data.jpa.repository.JpaRepository

interface ClinicMembershipRepository : JpaRepository<ClinicMembership, ClinicMembershipId>
