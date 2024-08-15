plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.weronka.golonka"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}