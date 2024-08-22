package com.weronka.golonka

import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV1LambdaFunction
import org.http4k.serverless.ApiGatewayV2LambdaFunction

fun main() {
    // Simulates Lambda behaviour locally
    val app =
        LambdaApplication(
            mapOf(
                "LOCAL_AWS_CONFIG__ENDPOINT_OVERRIDE" to "http://local-aws:4566",
                "LOCAL_AWS_CONFIG__SECRET_ACCESS_KEY" to "test",
                "LOCAL_AWS_CONFIG__ACCESS_KEY_ID" to "test",
                "LOCAL_AWS_CONFIG__REGION" to "us-east-1",
            ),
        )

    app.asServer(SunHttp(8099)).start()
}

@Suppress("unused")
class LocalPolyBuildApi : ApiGatewayV1LambdaFunction(LambdaApplication)

@Suppress("unused")
class PolyBuildApi : ApiGatewayV2LambdaFunction(LambdaApplication)
