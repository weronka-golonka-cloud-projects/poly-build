package com.weronka.golonka.http.domain.dto

import com.fasterxml.jackson.databind.ObjectMapper
import org.geojson.FeatureCollection
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.Body
import com.weronka.golonka.model.BuildingSites as DbBuildingSite

data class SplitBuildingLimitsResponse(
    val id: String,
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection,
    val splitBuildingLimits: FeatureCollection,
) {
    companion object {
        val lens = Body.auto<SplitBuildingLimitsResponse>().toLens()

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
