package com.weronka.golonka.http

import com.weronka.golonka.exceptions.BuildingSiteDoesNotExistsException
import com.weronka.golonka.exceptions.UnexpectedError
import com.weronka.golonka.http.domain.dto.ErrorResponse
import com.weronka.golonka.http.domain.dto.asResponse
import com.weronka.golonka.service.CalculatingSplitsFailedException
import com.weronka.golonka.service.InaccurateHeightPlateausException
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Status

private val logger = KotlinLogging.logger {}

val catchExceptionsFilter =
    Filter { next: HttpHandler ->
        { request: Request ->
            try {
                next(request)
            } catch (error: Throwable) {
                logger.error { "Catch exceptions filter: ${error.printStackTrace()}" }
                if (error is Exception) {
                    when (error) {
                        is InaccurateHeightPlateausException ->
                            ErrorResponse(
                                title = "Inaccurate height plateaus",
                                message = error.message,
                                code = Status.BAD_REQUEST.code,
                            ).asResponse()

                        is CalculatingSplitsFailedException ->
                            ErrorResponse(
                                title = "Failed splits calculation",
                                message = error.message,
                                code = Status.BAD_REQUEST.code,
                            ).asResponse()

                        is BuildingSiteDoesNotExistsException ->
                            ErrorResponse(
                                title = "Building Site not found",
                                message = error.message,
                                code = Status.NOT_FOUND.code,
                            ).asResponse()

                        is UnexpectedError ->
                            ErrorResponse(
                                title = "Unexpected error",
                                message = error.message,
                                code = Status.INTERNAL_SERVER_ERROR.code,
                            ).asResponse()

                        else ->
                            ErrorResponse(
                                title = "Unexpected error",
                                message = error.message,
                                code = Status.INTERNAL_SERVER_ERROR.code,
                            ).asResponse()
                    }
                } else {
                    throw error
                }
            }
        }
    }
