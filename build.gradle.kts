plugins {
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
}

group = "dev.zornov"
version = "1.3"

gradlePlugin {
    plugins {
        create("uploader") {
            id = "dev.zornov.uploader"
            implementationClass = "dev.zornov.uploader.UploaderPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.logback)
    implementation(libs.jsch)
    implementation(libs.kaml)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}

kotlin {
    jvmToolchain(21)
}
