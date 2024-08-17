package com.weronka.golonka.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID

@DynamoDbBean
class BuildingSites(
    @get:DynamoDbPartitionKey
    var id: String = UUID.randomUUID().toString(),
    var buildingLimits: String = "",
    var heightPlateaus: String = "",
    var splitBuildingLimits: String = ""
)