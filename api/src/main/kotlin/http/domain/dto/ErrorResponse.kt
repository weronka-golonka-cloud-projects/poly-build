package com.weronka.golonka.http.domain.dto

import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto

data class ErrorResponse(
    val title: String,
    val code: Int,
    val message: String?,
)

fun ErrorResponse.asResponse() =
    Response(Status(this.code, null)).with(
        Body.auto<ErrorResponse>().toLens() of this,
    )
