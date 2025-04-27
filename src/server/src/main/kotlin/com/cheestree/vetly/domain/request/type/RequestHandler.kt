package com.cheestree.vetly.domain.request.type

import com.cheestree.vetly.domain.request.Request

interface RequestHandler {
    fun canHandle(
        target: RequestTarget,
        action: RequestAction,
    ): Boolean

    fun execute(request: Request)
}
