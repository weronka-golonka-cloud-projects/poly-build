package com.weronka.golonka

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

data class Configuration(
    val serverPort: String,
    val dynamoDb: DynamoDbConfiguration,
) {
    companion object {
        fun load() =
            ConfigLoaderBuilder
                .default()
                .addResourceSource("application.conf")
                .build()
                .loadConfigOrThrow<Configuration>()
    }
}

data class DynamoDbConfiguration(
    val secretAccessKey: String,
    val accessKeyId: String,
    val region: String,
)
