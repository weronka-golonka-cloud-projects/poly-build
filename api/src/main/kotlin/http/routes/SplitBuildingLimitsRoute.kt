package com.weronka.golonka.http.routes

import com.weronka.golonka.http.domain.BuildingSite
import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsRequest
import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsResponse
import com.weronka.golonka.repository.BuildingSitesRepository
import com.weronka.golonka.service.BuildingLimitSplitter
import org.geojson.Feature
import org.geojson.FeatureCollection
import org.geojson.LngLatAlt
import org.geojson.Polygon
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import java.util.UUID

fun splitBuildingWithLimitsRoute(
    buildingLimitSplitter: BuildingLimitSplitter,
    buildingSitesRepository: BuildingSitesRepository,
): ContractRoute {
    val exampleBuildingLimits = FeatureCollection()
    val buildingLimitsFeature = Feature()
    buildingLimitsFeature.geometry =
        Polygon(
            LngLatAlt(0.0, 0.0),
            LngLatAlt(10.0, 0.0),
            LngLatAlt(10.0, 10.0),
            LngLatAlt(0.0, 10.0),
            LngLatAlt(0.0, 0.0),
        )
    exampleBuildingLimits.add(buildingLimitsFeature)
    val exampleHeightPlateaus = FeatureCollection()
    val heightPlateauFeature1 = Feature()
    val heightPlateauFeature2 = Feature()

    heightPlateauFeature1.properties =
        mapOf(
            "elevation" to 5.0,
        )
    heightPlateauFeature1.geometry =
        Polygon(
            LngLatAlt(0.0, 0.0),
            LngLatAlt(5.0, 0.0),
            LngLatAlt(5.0, 10.0),
            LngLatAlt(0.0, 10.0),
            LngLatAlt(0.0, 0.0),
        )

    heightPlateauFeature2.properties =
        mapOf(
            "elevation" to 3.0,
        )
    heightPlateauFeature2.geometry =
        Polygon(
            LngLatAlt(5.0, 0.0),
            LngLatAlt(11.0, 0.0),
            LngLatAlt(11.0, 11.0),
            LngLatAlt(5.0, 11.0),
            LngLatAlt(5.0, 0.0),
        )

    exampleHeightPlateaus
        .add(heightPlateauFeature1)
        .add(heightPlateauFeature2)

    val exampleBody =
        SplitBuildingLimitsRequest.lens to
            SplitBuildingLimitsRequest(
                buildingLimits = exampleBuildingLimits,
                heightPlateaus = exampleHeightPlateaus,
            )

    val exampleSplit = FeatureCollection()
    val splitFeature1 = Feature()
    splitFeature1.properties =
        mapOf(
            "elevation" to 5.0,
        )
    splitFeature1.geometry =
        Polygon(
            LngLatAlt(0.0, 0.0),
            LngLatAlt(5.0, 0.0),
            LngLatAlt(5.0, 10.0),
            LngLatAlt(0.0, 10.0),
            LngLatAlt(0.0, 0.0),
        )

    val splitFeature2 = Feature()
    splitFeature2.properties =
        mapOf(
            "elevation" to 3.0,
        )
    splitFeature2.geometry =
        Polygon(
            LngLatAlt(5.0, 0.0),
            LngLatAlt(10.0, 0.0),
            LngLatAlt(10.0, 10.0),
            LngLatAlt(5.0, 10.0),
            LngLatAlt(5.0, 0.0),
        )

    exampleSplit.add(splitFeature1).add(splitFeature2)

    val exampleResponse =
        SplitBuildingLimitsResponse.lens to
            SplitBuildingLimitsResponse(
                id = UUID.randomUUID().toString(),
                buildingLimits = exampleBuildingLimits,
                heightPlateaus = exampleHeightPlateaus,
                splitBuildingLimits = exampleSplit,
            )

    return "/split" meta {
        summary = "Divides building limits according to provided height plateaus"
        receiving(exampleBody)
        returning(Status.CREATED, exampleResponse)
    } bindContract Method.POST to { req: Request ->
        val requestBody = SplitBuildingLimitsRequest.lens(req)

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

        Response(Status.CREATED).with(
            SplitBuildingLimitsResponse.lens of SplitBuildingLimitsResponse.fromDbObject(buildingSite),
        )
    }
}
