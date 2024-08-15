plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.weronka.golonka"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.27.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-jackson")

    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:5.8.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}