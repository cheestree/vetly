package com.cheestree.vetly.domain.annotation

import com.cheestree.vetly.domain.enums.Role

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProtectedRoute(val role: Role)