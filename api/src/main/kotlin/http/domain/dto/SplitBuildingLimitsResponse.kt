package com.weronka.golonka.http.domain.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.weronka.golonka.json.CustomJackson.autoBody
import org.geojson.FeatureCollection
import com.weronka.golonka.model.BuildingSites as DbBuildingSite

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SplitBuildingLimitsResponse(
    val id: String,
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection,
    val splitBuildingLimits: FeatureCollection,
) {
    companion object {
        val lens = autoBody<SplitBuildingLimitsResponse>().toLens()

        fun fromDbObject(obj: DbBuildingSite): SplitBuildingLimitsResponse {
            val mapper = ObjectMapper()
            return SplitBuildingLimitsResponse(
                id = obj.id,
                buildingLimits = mapper.readValue(obj.buildingLimits, FeatureCollection::class.java),
                heightPlateaus = mapper.readValue(obj.heightPlateaus, FeatureCollection::class.java),
                splitBuildingLimits = mapper.readValue(obj.splitBuildingLimits, FeatureCollection::class.java),
            )
        }
    }
}
