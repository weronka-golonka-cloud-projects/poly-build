package com.weronka.golonka.json

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings

object CustomJackson : ConfigurableJackson(
    KotlinModule
        .Builder()
        .build()
        .asConfigurable()
        .withStandardMappings()
        .done(),
)
