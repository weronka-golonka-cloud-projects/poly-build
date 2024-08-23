package com.weronka.golonka.repository

import com.weronka.golonka.DynamoDbClientProvider
import com.weronka.golonka.exceptions.BuildingSiteDoesNotExistsException
import com.weronka.golonka.exceptions.UnexpectedError
import com.weronka.golonka.model.BuildingSites
import software.amazon.awssdk.enhanced.dynamodb.Key

class BuildingSitesRepository(dynamoDbClientProvider: DynamoDbClientProvider) {
    private val buildingSitesTable = dynamoDbClientProvider.geBuildingSitesTable()

    fun createBuildingSite(newBuildingSite: BuildingSites): BuildingSites =
        runCatching {
            buildingSitesTable.putItem(newBuildingSite)

            newBuildingSite
        }.getOrElse { throw UnexpectedError("Failed to create new Building Site", it) }

    fun getBuildingSiteById(id: String): BuildingSites =
        runCatching {
            val key = Key.builder()
                .partitionValue(id)
                .build()

            buildingSitesTable.getItem(key)
        }.fold({
            if (it == null)
                throw BuildingSiteDoesNotExistsException("Cannot get Building Site with id $id as it does not exist")
            else return it
            },
            { throw UnexpectedError("Failed to get Building Site with id $id", it) })

    fun updateBuildingSite(newBuildingSite: BuildingSites): BuildingSites =
        runCatching {
            val key = Key.builder()
                .partitionValue(newBuildingSite.id)
                .build()

            val currentVersion = buildingSitesTable.getItem(key).also {
                if (it == null) throw BuildingSiteDoesNotExistsException("Building site with id ${newBuildingSite.id} does not exist")
            }
            buildingSitesTable.updateItem(currentVersion.copy(
                buildingLimits = newBuildingSite.buildingLimits,
                heightPlateaus = newBuildingSite.heightPlateaus,
                splitBuildingLimits = newBuildingSite.splitBuildingLimits
            ))
        }.getOrElse {
            if (it is BuildingSiteDoesNotExistsException) throw it
            else throw UnexpectedError("Failed to updated Building site", it)
        }

    fun deleteBuildingSite(id: String): BuildingSites =
        runCatching {
            val key = Key.builder()
                .partitionValue(id)
                .build()

            buildingSitesTable.deleteItem(key)
        }.fold({
            if (it == null)
                throw BuildingSiteDoesNotExistsException("Cannot delete Building Site with id $id as it does not exist")
            else return it },
            { throw UnexpectedError("Failed to delete Building Site with id $id", it) }
        )
}