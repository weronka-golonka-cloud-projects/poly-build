package com.weronka.golonka.http.routes

import com.weronka.golonka.http.domain.BuildingSite
import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsRequest
import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsResponse
import com.weronka.golonka.repository.BuildingSitesRepository
import com.weronka.golonka.service.BuildingLimitSplitter
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import java.util.UUID

// params: DB repository, splitter service
fun splitBuildingWithLimitsRoute(
    buildingLimitSplitter: BuildingLimitSplitter,
    buildingSitesRepository: BuildingSitesRepository,
): ContractRoute {
    val body = SplitBuildingLimitsRequest.lens

    // TODO add exception mapper

    return "/split" meta {
        receiving(body)
    } bindContract Method.POST to { req: Request ->
        val requestBody = body(req)

        val splits =
            buildingLimitSplitter.calculateSplits(
                requestBody.buildingLimits,
                requestBody.heightPlateaus,
            )
        val buildingSite =
            buildingSitesRepository.createBuildingSite(
                BuildingSite(
                    id = UUID.randomUUID(),
                    buildingLimits = requestBody.buildingLimits,
                    heightPlateaus = requestBody.heightPlateaus,
                    splitBuildingLimits = splits,
                ).toDbObject(),
            )

        Response(Status.OK).with(
            SplitBuildingLimitsResponse.lens of SplitBuildingLimitsResponse.fromDbObject(buildingSite),
        )
    }
}
