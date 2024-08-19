package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.weronka.golonka.service.BuildingLimitSplitter
import com.weronka.golonka.service.CalculatingSplitsFailedException
import com.weronka.golonka.service.InaccurateHeightPlateausException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.geojson.Feature
import org.geojson.FeatureCollection
import org.geojson.LngLatAlt
import org.geojson.Polygon
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.io.geojson.GeoJsonReader

class BuildingLimitSplitterTest :
    DescribeSpec({
        val reader = GeoJsonReader(GeometryFactory())
        val jsonMapper = ObjectMapper()
        val splitter = BuildingLimitSplitter()

        fun FeatureCollection.isEqual(other: FeatureCollection): Boolean =
            this.features.all { feature ->
                val featureAsGeometry = reader.read(jsonMapper.writeValueAsString(feature))
                val otherFeatureWithSameTopology =
                    other.features.find { otherFeature ->
                        val otherFeatureAsGeometry = reader.read(jsonMapper.writeValueAsString(otherFeature))
                        otherFeatureAsGeometry.equalsTopo(featureAsGeometry)
                    }
                otherFeatureWithSameTopology != null && feature.properties == otherFeatureWithSameTopology.properties
            }

        val buildingLimitsFeature1 =
            Feature().apply {
                geometry =
                    Polygon(
                        LngLatAlt(0.0, 0.0),
                        LngLatAlt(10.0, 0.0),
                        LngLatAlt(10.0, 10.0),
                        LngLatAlt(0.0, 10.0),
                        LngLatAlt(0.0, 0.0),
                    )
            }
        val buildingLimits1 =
            FeatureCollection()
                .add(buildingLimitsFeature1)

        val heightPlateauFeature1 =
            Feature().apply {
                geometry =
                    Polygon(
                        LngLatAlt(0.0, 0.0),
                        LngLatAlt(5.0, 0.0),
                        LngLatAlt(5.0, 10.0),
                        LngLatAlt(0.0, 10.0),
                        LngLatAlt(0.0, 0.0),
                    )
                properties =
                    mapOf(
                        "elevation" to 5.0,
                    )
            }
        val heightPlateauFeature2 =
            Feature().apply {
                geometry =
                    Polygon(
                        LngLatAlt(5.0, 0.0),
                        LngLatAlt(11.0, 0.0),
                        LngLatAlt(11.0, 11.0),
                        LngLatAlt(5.0, 11.0),
                        LngLatAlt(5.0, 0.0),
                    )
                properties =
                    mapOf(
                        "elevation" to 3.0,
                    )
            }
        val heightPlateaus =
            FeatureCollection()
                .add(heightPlateauFeature1)
                .add(heightPlateauFeature2)

        it("should calculate the splits for single building limits") {
            val expectedSplits =
                FeatureCollection()
                    .add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(0.0, 0.0),
                                    LngLatAlt(5.0, 0.0),
                                    LngLatAlt(5.0, 10.0),
                                    LngLatAlt(0.0, 10.0),
                                    LngLatAlt(0.0, 0.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 5.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(5.0, 0.0),
                                    LngLatAlt(10.0, 0.0),
                                    LngLatAlt(10.0, 10.0),
                                    LngLatAlt(5.0, 10.0),
                                    LngLatAlt(5.0, 0.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 3.0,
                                )
                        },
                    )

            val result = splitter.calculateSplits(buildingLimits1, heightPlateaus)

            result.features.size shouldBe 2
            result.isEqual(expectedSplits) shouldBe true
        }

        it("should throw InaccurateHeightPlateauException if height plateaus do not cover the building limit") {
            val nonCoveringHeightPlateauFeature1 =
                Feature().apply {
                    geometry =
                        Polygon(
                            LngLatAlt(2.0, 2.0),
                            LngLatAlt(8.0, 2.0),
                            LngLatAlt(8.0, 5.0),
                            LngLatAlt(2.0, 5.0),
                            LngLatAlt(2.0, 2.0),
                        )
                    properties =
                        mapOf(
                            "elevation" to 5.0,
                        )
                }
            val nonCoveringHeightPlateauFeature2 =
                Feature().apply {
                    geometry =
                        Polygon(
                            LngLatAlt(1.0, 1.0),
                            LngLatAlt(9.0, 1.0),
                            LngLatAlt(9.0, 9.0),
                            LngLatAlt(1.0, 9.0),
                            LngLatAlt(1.0, 1.0),
                        )
                    properties =
                        mapOf(
                            "elevation" to 3.0,
                        )
                }
            val nonCoveringHeightPlateaus =
                FeatureCollection()
                    .add(nonCoveringHeightPlateauFeature1)
                    .add(nonCoveringHeightPlateauFeature2)

            shouldThrow<InaccurateHeightPlateausException> {
                splitter.calculateSplits(buildingLimits1, nonCoveringHeightPlateaus)
            }
        }

        it("should calculate splits for multiple building limits") {
            val multipleBuildingLimits =
                FeatureCollection()
                    .add(buildingLimitsFeature1)
                    .add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(1.0, 11.0),
                                    LngLatAlt(4.0, 11.0),
                                    LngLatAlt(4.0, 13.0),
                                    LngLatAlt(1.0, 13.0),
                                    LngLatAlt(1.0, 11.0),
                                )
                        },
                    )

            val allHeightPlateaus =
                FeatureCollection()
                    .add(heightPlateauFeature1)
                    .add(heightPlateauFeature2)
                    .add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(0.0, 11.0),
                                    LngLatAlt(2.0, 11.0),
                                    LngLatAlt(2.0, 15.0),
                                    LngLatAlt(0.0, 14.0),
                                    LngLatAlt(0.0, 11.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 8.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(2.0, 11.0),
                                    LngLatAlt(5.0, 11.0),
                                    LngLatAlt(5.0, 12.0),
                                    LngLatAlt(2.0, 12.0),
                                    LngLatAlt(2.0, 11.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 11.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(2.0, 12.0),
                                    LngLatAlt(5.0, 12.0),
                                    LngLatAlt(5.0, 15.0),
                                    LngLatAlt(2.0, 15.0),
                                    LngLatAlt(2.0, 12.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 10.0,
                                )
                        },
                    )

            val expectedSplits =
                FeatureCollection()
                    .add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(0.0, 0.0),
                                    LngLatAlt(5.0, 0.0),
                                    LngLatAlt(5.0, 10.0),
                                    LngLatAlt(0.0, 10.0),
                                    LngLatAlt(0.0, 0.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 5.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(5.0, 0.0),
                                    LngLatAlt(10.0, 0.0),
                                    LngLatAlt(10.0, 10.0),
                                    LngLatAlt(5.0, 10.0),
                                    LngLatAlt(5.0, 0.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 3.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(1.0, 11.0),
                                    LngLatAlt(2.0, 11.0),
                                    LngLatAlt(2.0, 13.0),
                                    LngLatAlt(1.0, 13.0),
                                    LngLatAlt(1.0, 11.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 8.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(2.0, 11.0),
                                    LngLatAlt(4.0, 11.0),
                                    LngLatAlt(4.0, 12.0),
                                    LngLatAlt(2.0, 12.0),
                                    LngLatAlt(2.0, 11.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 11.0,
                                )
                        },
                    ).add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(2.0, 12.0),
                                    LngLatAlt(4.0, 12.0),
                                    LngLatAlt(4.0, 13.0),
                                    LngLatAlt(2.0, 13.0),
                                    LngLatAlt(2.0, 12.0),
                                )
                            properties =
                                mapOf(
                                    "elevation" to 10.0,
                                )
                        },
                    )

            val result = splitter.calculateSplits(multipleBuildingLimits, allHeightPlateaus)

            result.features.size shouldBe 5
            result.isEqual(expectedSplits) shouldBe true
        }

        it("should throw CalculatingSplitsFailedException on any other failure during calculation") {
            val invalidHeightPlateau =
                FeatureCollection()
                    .add(
                        Feature().apply {
                            geometry =
                                Polygon(
                                    LngLatAlt(2.0, 12.0),
                                    LngLatAlt(4.0, 12.0),
                                    LngLatAlt(4.0, 13.0),
                                )
                        },
                    )

            shouldThrow<CalculatingSplitsFailedException> {
                splitter.calculateSplits(buildingLimits1, invalidHeightPlateau)
            }
        }
    })
