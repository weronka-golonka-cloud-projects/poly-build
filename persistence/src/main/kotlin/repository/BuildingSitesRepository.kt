package com.weronka.golonka.repository

import com.weronka.golonka.DynamoDbClientProvider
import com.weronka.golonka.exceptions.UnexpectedError
import com.weronka.golonka.model.BuildingSite
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest

class BuildingSitesRepository(private val dynamoDbClientProvider: DynamoDbClientProvider) {
    private val buildingSitesTable = dynamoDbClientProvider.geBuildingSitesTable()

    fun createBuildingSite(newBuildingSite: BuildingSite): BuildingSite =
        runCatching {
            val response = buildingSitesTable.putItemWithResponse(
                PutItemEnhancedRequest
                    .builder(BuildingSite::class.java)
                    .item(newBuildingSite)
                    .build()
            )

            response.attributes()
        }.getOrElse { throw UnexpectedError("Failed to create new Building Site", it) }

    fun getBuildingSiteById(id: String): BuildingSite =
        runCatching {
            val key = Key.builder()
                .partitionValue(id)
                .build()

            buildingSitesTable.getItem(key)
        }.getOrElse { throw UnexpectedError("Failed to get Building Site by id $id", it) }

    fun updateBuildingSite(newBuildingSite: BuildingSite): BuildingSite =
        runCatching {
            val response = buildingSitesTable.updateItemWithResponse(
                UpdateItemEnhancedRequest
                    .builder(BuildingSite::class.java)
                    .item(newBuildingSite)
                    .build()
            )

            response.attributes()
        }.getOrElse { throw UnexpectedError("Failed to update Building Site ", it) }

    fun deleteBuildingSite(id: String): BuildingSite =
        runCatching {
            val key = Key.builder()
                .partitionValue(id)
                .build()

            buildingSitesTable.deleteItem(key)
        }.getOrElse { throw UnexpectedError("Failed to delete Building Site with id $id", it) }
}