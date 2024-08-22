package http.routes

import com.fasterxml.jackson.databind.ObjectMapper
import com.weronka.golonka.http.domain.dto.SplitBuildingLimitsRequest
import com.weronka.golonka.http.routes.splitBuildingWithLimitsRoute
import com.weronka.golonka.model.BuildingSites
import com.weronka.golonka.repository.BuildingSitesRepository
import com.weronka.golonka.service.BuildingLimitSplitter
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.geojson.FeatureCollection
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import java.util.UUID

class SplitBuildingLimitsRouteKtTest :
    DescribeSpec({
        val mockRepository = mockk<BuildingSitesRepository>()
        val mockSplitter = mockk<BuildingLimitSplitter>()

        val handler = splitBuildingWithLimitsRoute(mockSplitter, mockRepository)

        val mapper = ObjectMapper()
        val geoJson = mapper.writeValueAsString(FeatureCollection())

        beforeEach {
            every { mockRepository.createBuildingSite(any()) } returns
                BuildingSites(
                    id = UUID.randomUUID().toString(),
                    buildingLimits = geoJson,
                    heightPlateaus = geoJson,
                    splitBuildingLimits = geoJson,
                )
            every { mockSplitter.calculateSplits(any(), any()) } returns FeatureCollection()
        }

        it("should return http 201 on successful division") {
            val response =
                handler(
                    Request(Method.POST, "http://test-url/split").with(
                        SplitBuildingLimitsRequest.lens of
                            SplitBuildingLimitsRequest(
                                buildingLimits = FeatureCollection(),
                                heightPlateaus = FeatureCollection(),
                            ),
                    ),
                )

            response.status shouldBe Status.CREATED
        }

        it("should return http 400 if body request is invalid") {
            val response =
                handler(
                    Request(Method.POST, "http://test-url/split").body("Invalid body"),
                )

            response.status shouldBe Status.BAD_REQUEST
        }
    })
