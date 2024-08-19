package com.weronka.golonka.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.geojson.Feature
import org.geojson.FeatureCollection
import org.geojson.LngLatAlt
import org.geojson.Polygon
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.io.geojson.GeoJsonReader
import org.locationtech.jts.io.geojson.GeoJsonWriter

class BuildingLimitSplitter {
    private val mapper = ObjectMapper()
    private val geoJsonReader = GeoJsonReader(GeometryFactory())
    private val geoJsonWriter = GeoJsonWriter()

    init {
        geoJsonWriter.setEncodeCRS(false)
    }

    fun calculateSplits(
        buildingLimits: FeatureCollection,
        heightPlateaus: FeatureCollection,
    ): FeatureCollection =
        runCatching {
            val heightPlateausGeometryToProperties =
                heightPlateaus.associate {
                    geoJsonReader.read(mapper.writeValueAsString(it.geometry)) to it.properties
                }
            val heightPlateausAsString = mapper.writeValueAsString(heightPlateaus)

            val buildingLimitSplits = mutableListOf<Feature>()
            buildingLimits.features.forEach { limit ->
                val limitsAsString = mapper.writeValueAsString(limit.geometry)
                val limitsAsGeometry = geoJsonReader.read(limitsAsString)

                val intersectionsToProperties =
                    heightPlateausGeometryToProperties
                        .mapNotNull { (h, p) ->
                            val intersection = limitsAsGeometry.intersection(h)
                            if (intersection.isEmpty) {
                                null
                            } else {
                                intersection to p
                            }
                        }.toMap()
                val asSingleIntersection =
                    intersectionsToProperties.keys.reduce { first, second ->
                        first.union(second)
                    }

                if (!asSingleIntersection.covers(limitsAsGeometry)) {
                    throw InaccurateHeightPlateausException(
                        "Provided height plateaus do not cover the building limits," +
                            "Building Limit: $limitsAsString\n" +
                            "Height Plateaus: $heightPlateausAsString",
                    )
                }

                intersectionsToProperties.forEach { (polygon, properties) ->
                    val newFeature = Feature()
                    newFeature.properties = properties

                    val coordinates =
                        polygon.coordinates.map { coordinate ->
                            LngLatAlt(coordinate.x, coordinate.y)
                        }
                    newFeature.geometry = Polygon(coordinates)

                    buildingLimitSplits.add(newFeature)
                }
            }

            val splits = FeatureCollection()
            splits.addAll(buildingLimitSplits)

            splits
        }.getOrElse {
            if (it is InaccurateHeightPlateausException) {
                throw it
            } else {
                throw CalculatingSplitsFailedException("Failed to calculate splits", it)
            }
        }
}
