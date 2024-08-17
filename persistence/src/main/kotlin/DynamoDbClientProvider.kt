package com.weronka.golonka

import com.weronka.golonka.model.BuildingSite
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class DynamoDbClientProvider {
    private fun getDynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            // TODO add credentials
            .build()
    }

    private fun getDynamoDbEnhancedClient(): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getDynamoDbClient())
            .build()
    }

    fun geBuildingSitesTable(): DynamoDbTable<BuildingSite> {
        val tableSchema = TableSchema.fromBean(BuildingSite::class.java)
        return getDynamoDbEnhancedClient().table("BuildingSites", tableSchema)
    }
}