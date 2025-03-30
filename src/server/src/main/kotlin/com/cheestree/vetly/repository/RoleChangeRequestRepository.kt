package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.user.roles.RoleChangeRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleChangeRequestRepository : JpaRepository<RoleChangeRequest, UUID>