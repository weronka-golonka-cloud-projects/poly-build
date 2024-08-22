package com.weronka.golonka

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource

data class Configuration(
    val localAwsConfig: LocalAwsConfig? = null,
) {
    companion object {
        fun load(environment: Map<String, String>) =
            if (environment.isNotEmpty()) {
                ConfigLoaderBuilder
                    .default()
                    .addPropertySource(
                        EnvironmentVariablesPropertySource(
                            useUnderscoresAsSeparator = true,
                            allowUppercaseNames = true,
                            environmentVariableMap = { environment },
                        ),
                    ).build()
                    .loadConfigOrThrow<Configuration>()
            } else {
                Configuration()
            }
    }
}
