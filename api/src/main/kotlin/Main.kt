package com.weronka.golonka

import com.weronka.golonka.http.routes.splitBuildingWithLimitsRoute
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    // config
    val port = if (args.isNotEmpty()) args[0] else "5000"
    val baseUrl = if (args.size > 1) args[1] else "http://localhost:$port"

    // init DB repository
    // init service

    val globalFilters = DebuggingFilters.PrintRequestAndResponse().then(ServerFilters.Cors(UnsafeGlobalPermissive))

    globalFilters.then(
        contract {
            renderer = OpenApi3(ApiInfo("My great API", "v1.0"), Jackson)
            descriptionPath = "/openapi.json"

            routes += splitBuildingWithLimitsRoute()
        }
    ).asServer(Jetty(port.toInt())).start().block()
}