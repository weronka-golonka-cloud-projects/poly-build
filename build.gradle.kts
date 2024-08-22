plugins {
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.serialization") version "2.0.0" apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.weronka.golonka"
    version = "unspecified"

    repositories {
        mavenCentral()
    }

    dependencies {
        "testImplementation" ("io.mockk:mockk:1.13.12")
        "testImplementation" ("io.kotest:kotest-runner-junit5:5.9.1")
        "testImplementation" ("io.kotest:kotest-assertions-core:5.9.1")
        "testImplementation" ("io.kotest:kotest-assertions-core-jvm:5.9.1")
        "testImplementation" ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
        "testImplementation" ("org.junit.jupiter:junit-jupiter-api:5.9.0")
        "testRuntimeOnly" ("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
