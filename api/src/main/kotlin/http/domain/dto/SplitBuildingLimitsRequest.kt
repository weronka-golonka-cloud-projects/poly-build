package com.weronka.golonka.http.domain.dto

import org.geojson.FeatureCollection
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.Body

data class SplitBuildingLimitsRequest(
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection,
) {
    companion object {
        val lens = Body.auto<SplitBuildingLimitsRequest>().toLens()
    }
}
