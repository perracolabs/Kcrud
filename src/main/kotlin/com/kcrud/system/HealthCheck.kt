/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system

import com.kcrud.data.database.service.DatabaseManager
import com.kcrud.graphql.GraphQLFramework
import com.kcrud.security.snowflake.SnowflakeData
import com.kcrud.security.snowflake.SnowflakeFactory
import com.kcrud.settings.AppSettings
import com.kcrud.utils.DeploymentType
import com.kcrud.utils.NetworkUtils
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

/**
 * A simple health check data class.
 */
@Suppress("unused")
@Serializable
data class HealthCheck(
    val errors: MutableList<String> = mutableListOf(),
    val utc: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val local: LocalDateTime = utc.toInstant(TimeZone.UTC).toLocalDateTime(TimeZone.currentSystemDefault()),
    val machineId: Int = AppSettings.server.machineId,
    val deploymentType: DeploymentType = AppSettings.deployment.type,
    val developmentModeEnabled: Boolean = AppSettings.server.development,
    val databaseAlive: Boolean = DatabaseManager.ping(),
    val databasePoolSize: Int = AppSettings.database.connectionPoolSize,
    val databaseJDBC: String = AppSettings.database.jdbcDriver,
    val databaseJDBCUrl: String = AppSettings.database.jdbcUrl,
    val protocol: String = NetworkUtils.getProtocol(),
    val server: String = NetworkUtils.getServerUrl(),
    val allowedHosts: List<String> = AppSettings.cors.allowedHosts,
    val apiVersion: String = AppSettings.deployment.apiVersion,
    val jwtEnabled: Boolean = AppSettings.security.jwt.isEnabled,
    val basicAuthEnabled: Boolean = AppSettings.security.basicAuth.isEnabled,
    val graphQLEnabled: Boolean = AppSettings.graphql.isEnabled,
    val graphQLFramework: GraphQLFramework = AppSettings.graphql.framework,
    val graphQLPlayground: Boolean = AppSettings.graphql.playground,
    val graphQLDumpSchemaEnabled: Boolean = AppSettings.graphql.dumpSchema,
    val docsEnabled: Boolean = AppSettings.docs.isEnabled,
    val snowflakeTestId: String = SnowflakeFactory.nextId(),
    val snowflakeTestResult: SnowflakeData = SnowflakeFactory.parse(id = snowflakeTestId)
) {
    init {
        if (!databaseAlive)
            errors.add("Database is not alive.")

        if (deploymentType == DeploymentType.PROD) {
            if (allowedHosts.isEmpty() or allowedHosts.contains("*"))
                errors.add("Allowing all hosts in '$deploymentType' environment.")

            if (developmentModeEnabled)
                errors.add("Development mode is enabled in '$deploymentType' environment.")

            if (graphQLPlayground)
                errors.add("GraphQL Playground is enabled in '$deploymentType' environment.")

            if (graphQLDumpSchemaEnabled)
                errors.add("GraphQL Schema Dump is enabled in '$deploymentType' environment.")

            if (docsEnabled)
                errors.add("Docs are enabled in '$deploymentType' environment.")
        }

        if (errors.isEmpty()) {
            errors.add("No Errors Detected.")
        }
    }
}
