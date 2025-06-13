package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException.ValidationException
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestHandler
import org.springframework.stereotype.Component

@Component
class RequestExecutor(
    private val requestHandlers: List<RequestHandler>,
) {
    fun execute(request: Request) {
        val handler =
            requestHandlers.find { it.canHandle(request.target, request.action) }
                ?: throw ValidationException(
                    "Unsupported request: ${request.action.name} operation on ${request.target.name}",
                )
        handler.execute(request)
    }
}
