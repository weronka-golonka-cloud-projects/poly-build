plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.27.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-jackson")

//    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:5.8.0")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.locationtech.jts.io:jts-io-common:1.19.0")
    implementation("de.grundid.opendatalab:geojson-jackson:1.14")

    implementation(project(":persistence"))

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(16)
}