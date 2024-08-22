package com.weronka.golonka.http.domain.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.weronka.golonka.json.CustomJackson.autoBody
import org.geojson.FeatureCollection

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SplitBuildingLimitsRequest(
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection,
) {
    companion object {
        val lens = autoBody<SplitBuildingLimitsRequest>().toLens()
    }
}
