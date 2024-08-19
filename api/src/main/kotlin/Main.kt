package com.weronka.golonka

import com.weronka.golonka.http.catchExceptionsFilter
import com.weronka.golonka.http.domain.dto.ErrorResponse
import com.weronka.golonka.http.domain.dto.asResponse
import com.weronka.golonka.http.routes.splitBuildingWithLimitsRoute
import com.weronka.golonka.repository.BuildingSitesRepository
import com.weronka.golonka.service.BuildingLimitSplitter
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val config = Configuration.load()

    // TODO use STS
    val dynamoDbClientProvider =
        DynamoDbClientProvider(
            secretAccessKey = config.dynamoDb.secretAccessKey,
            accessKeyId = config.dynamoDb.accessKeyId,
            region = config.dynamoDb.region,
        )
    val repository = BuildingSitesRepository(dynamoDbClientProvider)
    val buildingLimitSplitter = BuildingLimitSplitter()

    val globalFilters =
        DebuggingFilters
            .PrintRequestAndResponse()
            .then(
                ServerFilters.CatchLensFailure { lensFailure ->
                    ErrorResponse(
                        title = "Bad request",
                        message = lensFailure.message,
                        code = Status.BAD_REQUEST.code,
                    ).asResponse()
                },
            ).then(catchExceptionsFilter)
            .then(ServerFilters.Cors(UnsafeGlobalPermissive))

    globalFilters
        .then(
            contract {
                renderer = OpenApi3(ApiInfo("Poly Build API", "v1.0"), Jackson)
                descriptionPath = "/openapi.json"

                routes +=
                    splitBuildingWithLimitsRoute(
                        buildingLimitSplitter,
                        repository,
                    )
            },
        ).asServer(Jetty(config.serverPort.toInt()))
        .start()
        .block()
}
