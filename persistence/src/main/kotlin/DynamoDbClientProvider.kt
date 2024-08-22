package com.weronka.golonka

import com.weronka.golonka.model.BuildingSites
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

data class LocalAwsConfig(
    val endpointOverride: URI,
    val secretAccessKey: String,
    val accessKeyId: String,
    val region: String,
)

class DynamoDbClientProvider(
    private val localConfig: LocalAwsConfig? = null,
) {
    private fun getDynamoDbClient(): DynamoDbClient =
        DynamoDbClient
            .builder()
            .apply {
                if (localConfig != null) {
                    endpointOverride(localConfig.endpointOverride)
                    credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                localConfig.accessKeyId,
                                localConfig.secretAccessKey,
                            ),
                        ),
                    ).region(Region.of(localConfig.region))
                }
            }.build()

    private fun getDynamoDbEnhancedClient(): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient
            .builder()
            .dynamoDbClient(getDynamoDbClient())
            .build()

    fun geBuildingSitesTable(): DynamoDbTable<BuildingSites> {
        val tableSchema = TableSchema.fromBean(BuildingSites::class.java)
        return getDynamoDbEnhancedClient().table("BuildingSites", tableSchema)
    }
}
