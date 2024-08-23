package com.weronka.golonka.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute

@DynamoDbBean
data class BuildingSites(
    @get:DynamoDbPartitionKey
    var id: String = UUID.randomUUID().toString(),
    var buildingLimits: String = "",
    var heightPlateaus: String = "",
    var splitBuildingLimits: String = "",
    @get:DynamoDbVersionAttribute
    var version: Long? = null
)