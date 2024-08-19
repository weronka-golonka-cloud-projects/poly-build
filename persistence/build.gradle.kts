plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.27.7"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation("org.testcontainers:testcontainers:1.20.1")
    testImplementation("org.testcontainers:localstack:1.20.1")
}

kotlin {
    jvmToolchain(16)
}
