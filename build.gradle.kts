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

publishing {
    repositories {
        maven {
            name = "reposlate"
            url = uri("http://94.156.170.35:4040/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
            isAllowInsecureProtocol = true
        }
    }
}


kotlin {
    jvmToolchain(21)
}
