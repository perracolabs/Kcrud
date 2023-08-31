plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.3"
    kotlin("plugin.serialization") version "1.9.10"
}

group = "com.kcrud"
version = "1.0.0"

application {
    mainClass.set("com.kcrud.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    jvmToolchain(11)
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    // Ktor
    // https://github.com/ktorio/ktor
    val ktorVersion = "2.3.3"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Kotlinx Serialization
    // https://github.com/Kotlin/kotlinx.serialization
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
    val serializationVersion = "1.6.0"
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$serializationVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$serializationVersion")

    // Serializable Date Time
    // https://github.com/Kotlin/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // 'Conf' type safety
    // https://github.com/lightbend/config
    implementation("com.typesafe:config:1.4.2")

    // Exposed ORM
    // https://github.com/JetBrains/Exposed
    val exposedVersion = "0.43.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    // Koin
    // https://insert-koin.io/docs/quickstart/ktor
    // https://github.com/InsertKoinIO/koin-getting-started
    val koinVersion = "3.4.3"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // H2 database
    // https://github.com/h2database/h2database
    implementation("com.h2database:h2:2.1.210")

    // SQLite database
    // https://github.com/sqlite/sqlite
    implementation("org.xerial:sqlite-jdbc:3.42.0.1")

    // GraphQL
    // https://github.com/aPureBase/KGraphQL
    implementation("com.apurebase:kgraphql:0.19.0")
    implementation("com.apurebase:kgraphql-ktor:0.19.0")

    implementation("io.mockk:mockk:1.11.0")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.slf4j:slf4j-api:1.7.32")
    testImplementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.insert-koin:koin-test:3.4.3")
}
