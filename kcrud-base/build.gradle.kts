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

group = "kcrud.base"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {

    implementation(libs.database.h2)
    implementation(libs.database.sqlite)

    implementation(libs.docs.commons.codec)
    implementation(libs.docs.gson)
    implementation(libs.docs.swagger)
    implementation(libs.docs.swagger.generators)
    implementation(libs.docs.openapi)

    implementation(libs.kotlinx.datetime)

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.autoHeadResponse)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.defaultHeaders)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.hsts)
    implementation(libs.ktor.server.rateLimit)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.tests)
    implementation(libs.ktor.server.test.host)

    implementation(libs.exposed.core)
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.jdbc)

    implementation(libs.graphql.expedia.generator)
    implementation(libs.graphql.expedia.server)
    implementation(libs.graphql.kgraphql)
    implementation(libs.graphql.kgraphql.ktor)

    implementation(libs.hikariCP)

    implementation(libs.logback.classic)

    implementation(libs.test.kotlin.junit)
    implementation(libs.test.mockk)
    implementation(libs.test.mockito.kotlin)

    implementation(libs.typesafe.config)
}
