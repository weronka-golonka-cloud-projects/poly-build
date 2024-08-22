plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.27.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-format-kotlinx-serialization")
    implementation("org.http4k:http4k-serverless-lambda")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.locationtech.jts.io:jts-io-common:1.19.0")
    implementation("de.grundid.opendatalab:geojson-jackson:1.14")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    runtimeOnly("com.sksamuel.hoplite:hoplite-yaml:2.7.5")

    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.5.7")

    implementation(project(":persistence"))

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-framework-datatest:5.9.1")
}

kotlin {
    jvmToolchain(16)
}

tasks.register<Zip>("buildLambdaZip") {
    dependsOn("build")
    from(tasks.compileKotlin)
    from(tasks.processResources)
    into("lib") {
        from(configurations.runtimeClasspath)
        from(configurations.compileClasspath)
    }
}
