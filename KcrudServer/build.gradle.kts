/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.graphql.expedia)
}

group = "kcrud.server"
version = "1.0.0"

application {
    mainClass.set("kcrud.server.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("ktor.jar")
    }
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {

    implementation(project(":KcrudCore"))

    implementation(libs.docs.commons.codec)
    implementation(libs.docs.gson)

    implementation(libs.kotlinx.datetime)

    implementation(libs.ktor.serialization.kotlinxjson)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authjwt)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.htmlbuilder)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.ratelimit)
    implementation(libs.ktor.server.tests)
    implementation(libs.ktor.server.testhost)

    implementation(libs.exposed.core)
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlindatetime)

    implementation(libs.graphql.expedia.server)
    implementation(libs.graphql.kgraphql.ktor)

    implementation(libs.koin.ktor)
    implementation(libs.koin.loggerslf4j)
    implementation(libs.koin.test)

    implementation(libs.test.kotlin.junit)
    implementation(libs.test.mockk)
    implementation(libs.test.mockito.kotlin)
}
