/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system.healthcheck

import com.kcrud.data.database.manager.DatabaseManager
import com.kcrud.system.healthcheck.checks.*
import kotlinx.serialization.Serializable

/**
 * A simple health check data class.
 */
@Serializable
data class HealthCheck(
    val errors: MutableList<String> = mutableListOf(),
    val server: ServerCheck = ServerCheck(),
    val security: SecurityCheck = SecurityCheck(),
    val database: DatabaseCheck = DatabaseManager.getHealthCheck(),
    val application: ApplicationCheck = ApplicationCheck(),
    val graphQL: GraphQLCheck = GraphQLCheck(),
    val snowflake: SnowflakeCheck = SnowflakeCheck(),
) {
    init {
        errors.addAll(server.errors)
        errors.addAll(security.errors)
        errors.addAll(database.errors)
        errors.addAll(application.errors)
        errors.addAll(graphQL.errors)
        errors.addAll(snowflake.errors)

        if (errors.isEmpty()) {
            errors.add("No Errors Detected.")
        }
    }
}