package com.weronka.golonka.http.domain

import com.mapbox.geojson.FeatureCollection

data class SplitBuildingLimitsRequest(
    val buildingLimits: FeatureCollection,
    val heightPlateaus: FeatureCollection
)