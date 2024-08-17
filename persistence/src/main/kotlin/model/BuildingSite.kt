package com.weronka.golonka.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID

@DynamoDbBean
data class BuildingSite(
    @get:DynamoDbPartitionKey
    val id: String = UUID.randomUUID().toString(),
    val buildingLimits: String,
    val heightPlateaus: String,
    val splitBuildingLimits: String
)