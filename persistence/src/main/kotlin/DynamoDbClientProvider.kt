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

class DynamoDbClientProvider(
    private val endpointOverride: URI? = null,
    private val secretAccessKey: String,
    private val accessKeyId: String,
    private val region: String
) {
    private fun getDynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            // TODO use STS?
            .apply {
                if (endpointOverride != null) {
                    this.endpointOverride(endpointOverride)
                }
            }
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        secretAccessKey,
                        accessKeyId
                    )
                )
            )
            .build()
    }

    private fun getDynamoDbEnhancedClient(): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getDynamoDbClient())
            .build()
    }

    fun geBuildingSitesTable(): DynamoDbTable<BuildingSites> {
        val tableSchema = TableSchema.fromBean(BuildingSites::class.java)
        return getDynamoDbEnhancedClient().table("BuildingSites", tableSchema)
    }
}