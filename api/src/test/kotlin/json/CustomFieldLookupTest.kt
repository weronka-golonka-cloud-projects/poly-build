package json

import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsRequest
import com.weronka.golonka.json.CustomFieldLookup
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.geojson.Feature
import org.geojson.FeatureCollection
import org.geojson.Polygon

class CustomFieldLookupTest :
    DescribeSpec({
        val fieldLookup = CustomFieldLookup()

        describe("GeoJsonObject tests") {
            val target = Feature()

            it("should retrieve field 'type' based on annotations if target is a subclass of GeoJsonObject") {
                val fieldName = "type"

                val field = fieldLookup(target, fieldName)

                field.isNullable shouldBe false
                field.value shouldBe target::class.java.simpleName
            }

            it("should retrieve field 'type' from classes that don't directly inherit from GeoJsonObject") {
                val indirectTarget = Polygon()
                val fieldName = "type"

                val field = fieldLookup(indirectTarget, fieldName)

                field.isNullable shouldBe false
                field.value shouldBe indirectTarget::class.java.simpleName
            }

            it("should retrieve regular field of GeoJsonObject subclass") {
                val fieldName = "geometry"
                val polygon = Polygon()
                target.geometry = polygon

                val field =
                    fieldLookup(
                        target,
                        fieldName,
                    )

                field.isNullable shouldBe true
                field.value shouldBe polygon
            }
        }

        describe("other classes") {
            it("should correctly retrieve field") {
                val featureCollection = FeatureCollection()
                val target =
                    SplitBuildingLimitsRequest(
                        buildingLimits = featureCollection,
                        heightPlateaus = featureCollection,
                    )
                val fieldName = "buildingLimits"

                val field = fieldLookup(target, fieldName)

                field.isNullable shouldBe false
                field.value shouldBe featureCollection
            }
        }
    })
