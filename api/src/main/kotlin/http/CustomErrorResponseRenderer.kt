package com.weronka.golonka.http

import com.weronka.golonka.http.domain.dto.ErrorResponse
import com.weronka.golonka.http.domain.dto.asResponse
import org.http4k.contract.ErrorResponseRenderer
import org.http4k.core.Response
import org.http4k.lens.LensFailure

class CustomErrorResponseRenderer : ErrorResponseRenderer {
    override fun badRequest(lensFailure: LensFailure): Response =
        ErrorResponse(
            title = "Bad request",
            message = lensFailure.message,
            code = 400,
        ).asResponse()

    override fun notFound(): Response =
        ErrorResponse(
            title = "Not found",
            message = null,
            code = 404,
        ).asResponse()
}
