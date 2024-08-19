package com.weronka.golonka.http.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.geojson.FeatureCollection
import java.util.UUID
import com.weronka.golonka.model.BuildingSites as DbBuildingSite

data class BuildingSite(
    val id: UUID,
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection,
    val splitBuildingLimits: FeatureCollection,
) {
    fun toDbObject(): DbBuildingSite {
        val mapper = ObjectMapper()
        return DbBuildingSite(
            id = this.id.toString(),
            buildingLimits = mapper.writeValueAsString(this.buildingLimits),
            heightPlateaus = mapper.writeValueAsString(this.heightPlateaus),
            splitBuildingLimits = mapper.writeValueAsString(this.splitBuildingLimits),
        )
    }
}
