/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes.main

import com.kcrud.data.database.shared.DatabaseManager
import com.kcrud.graphql.GraphQLFramework
import com.kcrud.security.snowflake.SnowflakeData
import com.kcrud.security.snowflake.SnowflakeFactory
import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.NetworkUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

/**
 * Defines the health check and snowflake parser endpoints.
 *
 * The endpoint ("/health") provides a simple way to monitor the operational status
 * of the application.
 *
 * The snowflake endpoint ("/snowflake/{id}") parses a Snowflake ID and returns the
 * components extracted from the ID.
 *
 * The current implementation checks the basic readiness of the application. Future
 * enhancements could include more complex health checks, like database connectivity,
 * external service availability, or other critical component checks.
 */
fun Route.systemRoute() {
    authenticate(SettingsProvider.security.basicAuth.providerName) {
        get("/health") {
            call.respond(HealthCheckResponse())
        }

        get("/snowflake/{id}") {
            call.respond(HttpStatusCode.OK, SnowflakeFactory.parse(id = call.parameters["id"]!!))
        }
    }

    NetworkUtils.logEndpoints(
        reason = "System endpoints. Requires basic authentication credentials",
        endpoints = listOf("health", "snowflake/${SnowflakeFactory.nextId()}")
    )
}

@Serializable
data class HealthCheckResponse(
    val ready: Boolean = true,
    val machineId: Int = SettingsProvider.global.machineId,
    val development: Boolean = SettingsProvider.global.development,
    val databaseAlive: Boolean = DatabaseManager.ping(),
    val databasePoolSize: Int = SettingsProvider.database.connectionPoolSize,
    val databaseJDBC: String = SettingsProvider.database.jdbcDriver,
    val databaseJDBCUrl: String = SettingsProvider.database.jdbcUrl,
    val protocol: String = NetworkUtils.getProtocol(),
    val server: String = NetworkUtils.getServerUrl(),
    val apiVersion: String = SettingsProvider.deployment.apiVersion,
    val jwtEnabled: Boolean = SettingsProvider.security.jwt.isEnabled,
    val basicAuthEnabled: Boolean = SettingsProvider.security.basicAuth.isEnabled,
    val graphQLEnabled: Boolean = SettingsProvider.graphql.isEnabled,
    val graphQLFramework: GraphQLFramework = SettingsProvider.graphql.framework,
    val graphQLPlayground: Boolean = SettingsProvider.graphql.playground,
    val graphQLDumpSchemaEnabled: Boolean = SettingsProvider.graphql.dumpSchema,
    val docsEnabled: Boolean = SettingsProvider.docs.isEnabled,
    val snowflakeTestId: String =  SnowflakeFactory.nextId(),
    val snowflakeTestResult: SnowflakeData = SnowflakeFactory.parse(id = snowflakeTestId),
    val utc: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val local: LocalDateTime = utc.toInstant(TimeZone.UTC).toLocalDateTime(TimeZone.currentSystemDefault())
)
