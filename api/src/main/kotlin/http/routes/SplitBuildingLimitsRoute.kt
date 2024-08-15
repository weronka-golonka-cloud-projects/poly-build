package com.weronka.golonka.http.routes

import com.weronka.golonka.http.domain.SplitBuildingLimitsRequest
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.*

// params: DB repository, splitter service
fun splitBuildingWithLimitsRoute(): ContractRoute {

    val body = Body.auto<SplitBuildingLimitsRequest>().toLens()

    return "/split" meta {
        receiving(body)
    } bindContract Method.POST to { req: Request ->
        val requestBody = body(req)

        // calculate new polygons and store in DB

        Response(Status.OK)
    }
}