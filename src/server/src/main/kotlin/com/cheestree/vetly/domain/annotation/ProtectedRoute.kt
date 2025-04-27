package com.cheestree.vetly.domain.annotation

import com.cheestree.vetly.domain.user.roles.Role

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProtectedRoute(val role: Role)
