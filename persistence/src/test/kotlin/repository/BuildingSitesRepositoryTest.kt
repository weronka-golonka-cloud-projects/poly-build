package repository

import com.weronka.golonka.DynamoDbClientProvider
import com.weronka.golonka.LocalAwsConfig
import com.weronka.golonka.exceptions.BuildingSiteDoesNotExistsException
import com.weronka.golonka.model.BuildingSites
import com.weronka.golonka.repository.BuildingSitesRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import java.util.UUID

class BuildingSitesRepositoryTest :
    DescribeSpec({
        val localStackImage = DockerImageName.parse("localstack/localstack:3.5.0")
        val localStack =
            LocalStackContainer(localStackImage)
                .withServices(LocalStackContainer.Service.DYNAMODB)

        lateinit var dynamoDbProvider: DynamoDbClientProvider
        lateinit var repository: BuildingSitesRepository

        val testId = UUID.randomUUID().toString()
        val testBuildingSite =
            BuildingSites(
                id = testId,
                buildingLimits = "test",
                heightPlateaus = "test",
                splitBuildingLimits = "test",
            )

        fun initDynamoDb() {
            val initScript = javaClass.classLoader.getResource("create-table.sh")
            if (initScript != null) {
                val process =
                    ProcessBuilder(
                        "bash",
                        initScript.path,
                        localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString(),
                        localStack.accessKey,
                        localStack.secretKey,
                    ).redirectErrorStream(true)
                        .start()

                process.waitFor()
            } else {
                throw IllegalStateException("Initialization script not found in resources")
            }
        }

        beforeEach {
            localStack.start()
            initDynamoDb()

            dynamoDbProvider =
                DynamoDbClientProvider(
                    localConfig =
                        LocalAwsConfig(
                            endpointOverride = localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB),
                            secretAccessKey = localStack.secretKey,
                            accessKeyId = localStack.accessKey,
                            region = localStack.region,
                        ),
                )
            repository = BuildingSitesRepository(dynamoDbProvider)
        }

        afterEach {
            localStack.close()
        }

        describe("create") {
            it("should return Building Site object on successful creation") {
                val result = repository.createBuildingSite(testBuildingSite)

                result shouldBe testBuildingSite
            }
        }

        describe("get by id") {
            it("should return Building Site object on successful query") {
                repository.createBuildingSite(testBuildingSite)
                val result = repository.getBuildingSiteById(testId)

                result.id shouldBe testBuildingSite.id
            }

            it("should throw BuildingSiteDoesNotExistsException if item does not exist") {
                shouldThrow<BuildingSiteDoesNotExistsException> {
                    repository.getBuildingSiteById(testId)
                }
            }
        }

        describe("update") {
            val newBuildingSite =
                BuildingSites(
                    id = testId,
                    buildingLimits = testBuildingSite.buildingLimits,
                    heightPlateaus = "new value",
                    splitBuildingLimits = testBuildingSite.splitBuildingLimits,
                )

            it("should return updated Building Site object on successful update") {
                val result = repository.updateBuildingSite(newBuildingSite)

                result.id shouldBe newBuildingSite.id
                result.heightPlateaus shouldBe newBuildingSite.heightPlateaus
            }
        }

        describe("delete") {
            it("should return deleted Building Site object on successful deletion") {
                repository.createBuildingSite(testBuildingSite)
                val result = repository.deleteBuildingSite(testId)

                result.id shouldBe testBuildingSite.id
            }

            it("should throw BuildingSiteDoesNotExistsException if item for deletion does not exist") {
                shouldThrow<BuildingSiteDoesNotExistsException> {
                    repository.deleteBuildingSite(testId)
                }
            }
        }
    })
