import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("com.bmuschko.docker-remote-api")
}

group = "com.weronka.golonka"
version = "unspecified"

tasks.create("dockerBuild", DockerBuildImage::class) {
    inputDir.set(projectDir)
    images.add("poly-build-end-to-end-tests:latest")
}
