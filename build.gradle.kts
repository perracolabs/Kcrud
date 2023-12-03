/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    kotlin("plugin.serialization") version "1.9.21"
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
    val ktorVersion = "2.3.6"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")

    // Basic Authentication.
    // https://ktor.io/docs/basic.html
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    // JWT Authentication.
    // https://ktor.io/docs/jwt.html
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

    // Logging.
    // https://github.com/qos-ch/logback
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Kotlinx Serialization.
    // https://github.com/Kotlin/kotlinx.serialization
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // Serializable Date Time.
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
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    // Koin.
    // https://insert-koin.io/docs/quickstart/ktor
    // https://github.com/InsertKoinIO/koin
    // https://github.com/InsertKoinIO/koin-getting-started
    val koinVersion = "3.5.1"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // H2 database.
    // https://github.com/h2database/h2database
    // TODO: update once IntelliJ supports the latest driver.
    implementation("com.h2database:h2:2.1.210")

    // SQLite database.
    // https://github.com/sqlite/sqlite
    implementation("org.xerial:sqlite-jdbc:3.43.2.2")

    // GraphQL.
    // https://github.com/aPureBase/KGraphQL
    val apureBaseVersion = "0.19.0"
    implementation("com.apurebase:kgraphql:$apureBaseVersion")
    implementation("com.apurebase:kgraphql-ktor:$apureBaseVersion")

    implementation("io.mockk:mockk:1.13.8")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.21")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.slf4j:slf4j-api:2.0.9")
    testImplementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.insert-koin:koin-test:3.5.0")
}
