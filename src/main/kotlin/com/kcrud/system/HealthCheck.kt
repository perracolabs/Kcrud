/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system

import com.kcrud.data.database.shared.DatabaseManager
import com.kcrud.graphql.GraphQLFramework
import com.kcrud.security.snowflake.SnowflakeData
import com.kcrud.security.snowflake.SnowflakeFactory
import com.kcrud.settings.SettingsProvider
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
    val machineId: Int = SettingsProvider.server.machineId,
    val deploymentType: DeploymentType = SettingsProvider.deployment.type,
    val developmentModeEnabled: Boolean = SettingsProvider.server.development,
    val databaseAlive: Boolean = DatabaseManager.ping(),
    val databasePoolSize: Int = SettingsProvider.database.connectionPoolSize,
    val databaseJDBC: String = SettingsProvider.database.jdbcDriver,
    val databaseJDBCUrl: String = SettingsProvider.database.jdbcUrl,
    val protocol: String = NetworkUtils.getProtocol(),
    val server: String = NetworkUtils.getServerUrl(),
    val allowedHosts: List<String> = SettingsProvider.cors.allowedHosts,
    val apiVersion: String = SettingsProvider.deployment.apiVersion,
    val jwtEnabled: Boolean = SettingsProvider.security.jwt.isEnabled,
    val basicAuthEnabled: Boolean = SettingsProvider.security.basicAuth.isEnabled,
    val graphQLEnabled: Boolean = SettingsProvider.graphql.isEnabled,
    val graphQLFramework: GraphQLFramework = SettingsProvider.graphql.framework,
    val graphQLPlayground: Boolean = SettingsProvider.graphql.playground,
    val graphQLDumpSchemaEnabled: Boolean = SettingsProvider.graphql.dumpSchema,
    val docsEnabled: Boolean = SettingsProvider.docs.isEnabled,
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
