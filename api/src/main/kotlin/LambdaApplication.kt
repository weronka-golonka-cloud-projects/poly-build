package com.weronka.golonka

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.weronka.golonka.http.CustomErrorResponseRenderer
import com.weronka.golonka.http.catchExceptionsFilter
import com.weronka.golonka.http.domain.dto.ErrorResponse
import com.weronka.golonka.http.domain.dto.asResponse
import com.weronka.golonka.http.routes.splitBuildingWithLimitsRoute
import com.weronka.golonka.json.CustomFieldLookup
import com.weronka.golonka.json.CustomJackson
import com.weronka.golonka.repository.BuildingSitesRepository
import com.weronka.golonka.service.BuildingLimitSplitter
import mu.KotlinLogging
import org.http4k.contract.contract
import org.http4k.contract.jsonschema.v3.AutoJsonToJsonSchema
import org.http4k.contract.jsonschema.v3.FieldRetrieval
import org.http4k.contract.jsonschema.v3.PrimitivesFieldMetadataRetrievalStrategy
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.ApiRenderer
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.HttpHandler
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.serverless.AppLoader

private val logger = KotlinLogging.logger {}

object LambdaApplication : AppLoader {
    override fun invoke(env: Map<String, String>): HttpHandler {
        logger.info { "Initializing config with env $env" }
        val config = Configuration.load(env)

        logger.info { "Starting DynamoDB client provider" }
        val dynamoDbClientProvider = DynamoDbClientProvider(config.localAwsConfig)

        logger.info { "Starting repository" }
        val repository = BuildingSitesRepository(dynamoDbClientProvider)

        logger.info { "Starting service" }
        val buildingLimitSplitter = BuildingLimitSplitter()

        return polyBuildApp(repository, buildingLimitSplitter)
    }
}

fun polyBuildApp(
    repository: BuildingSitesRepository,
    buildingLimitSplitter: BuildingLimitSplitter,
): HttpHandler {
    val globalFilters =
        DebuggingFilters.PrintRequestAndResponse()
            .then(ServerFilters.Cors(UnsafeGlobalPermissive))
            .then(
                ServerFilters.CatchLensFailure { lensFailure ->
                    logger.error { "Caught Lens Failure: ${lensFailure.printStackTrace()}" }
                    ErrorResponse(
                        title = "Bad request",
                        message = lensFailure.message,
                        code = Status.BAD_REQUEST.code,
                    ).asResponse()
                },
            ).then(catchExceptionsFilter)

    return globalFilters
        .then(
            contract {
                renderer =
                    OpenApi3(
                        apiInfo = ApiInfo("Poly Build API", "v1.0"),
                        json = CustomJackson,
                        apiRenderer =
                            ApiRenderer.Auto(
                                CustomJackson,
                                AutoJsonToJsonSchema(
                                    CustomJackson,
                                    fieldRetrieval =
                                        FieldRetrieval.compose(
                                            CustomFieldLookup(
                                                renamingStrategy = { name ->
                                                    PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE.translate(name)
                                                },
                                                metadataRetrievalStrategy = PrimitivesFieldMetadataRetrievalStrategy,
                                            ),
                                        ),
                                ),
                            ),
                        errorResponseRenderer = CustomErrorResponseRenderer(),
                    )
                descriptionPath = "/openapi.json"

                routes +=
                    splitBuildingWithLimitsRoute(
                        buildingLimitSplitter,
                        repository,
                    )
            },
        )
}
