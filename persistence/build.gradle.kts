plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.weronka.golonka"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.27.7"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}