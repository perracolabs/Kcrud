/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.7"
    kotlin("plugin.serialization") version "1.9.21"
    id("com.expediagroup.graphql") version "7.0.2"
}

group = "com.kcrud"
version = "1.0.0"

application {
    mainClass.set("com.kcrud.ApplicationKt")

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

    // Ktor.
    // https://github.com/ktorio/ktor
    // https://github.com/ktorio/ktor/releases
    // https://api.ktor.io
    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")

    // Call Logging, Call Id.
    // https://ktor.io/docs/call-logging.html
    // https://ktor.io/docs/call-id.html
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id:$ktorVersion")

    // Basic Authentication.
    // https://ktor.io/docs/basic.html
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    // JWT Authentication.
    // https://ktor.io/docs/jwt.html
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

    // Logging.
    // https://github.com/qos-ch/logback
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation("ch.qos.logback:logback-classic:1.2.11")

    // Serialization.
    // https://github.com/Kotlin/kotlinx.serialization
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Serializable DateTime.
    // https://github.com/Kotlin/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // 'Conf' type safety.
    // https://github.com/lightbend/config
    implementation("com.typesafe:config:1.4.3")

    // Exposed ORM.
    // https://github.com/JetBrains/Exposed
    // https://github.com/JetBrains/Exposed/blob/main/docs/ChangeLog.md
    val exposedVersion = "0.45.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    // HikariCP (Database connection pooling)
    // https://github.com/brettwooldridge/HikariCP
    // https://ktor.io/docs/connection-pooling-caching.html
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Koin.
    // https://insert-koin.io/docs/quickstart/ktor
    // https://github.com/InsertKoinIO/koin
    // https://github.com/InsertKoinIO/koin-getting-started
    val koinVersion = "3.5.3"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")

    // H2 database.
    // https://github.com/h2database/h2database
    implementation("com.h2database:h2:2.2.224")

    // SQLite database.
    // https://github.com/sqlite/sqlite
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")

    // GraphQL with KGraphQL.
    // https://github.com/aPureBase/KGraphQL
    val kgraphqlVersion = "0.19.0"
    implementation("com.apurebase:kgraphql:$kgraphqlVersion")
    implementation("com.apurebase:kgraphql-ktor:$kgraphqlVersion")

    // GraphQL with ExpediaGroup.
    // https://opensource.expediagroup.com/graphql-kotlin/docs/server/ktor-server/ktor-overview
    // https://github.com/ExpediaGroup/graphql-kotlin/tree/master/servers/graphql-kotlin-ktor-server
    implementation("com.expediagroup:graphql-kotlin-ktor-server:7.0.2")

    // OpenAPI / SwaggerUI.
    // https://ktor.io/docs/swagger-ui.html#configure-swagger
    // https://github.com/swagger-api/swagger-codegen-generators
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.45")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("com.google.code.gson:gson:2.8.9")

    implementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.21")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}
