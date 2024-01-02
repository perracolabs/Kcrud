/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.healthcheck

import com.kcrud.admin.env.healthcheck.annotation.HealthCheckAPI
import com.kcrud.admin.env.healthcheck.checks.*
import com.kcrud.admin.env.healthcheck.utils.collectRoutes
import com.kcrud.data.database.DatabaseManager
import io.ktor.server.application.*
import kotlinx.serialization.Serializable

/**
 * A simple health check data class.
 */
@Suppress("MemberVisibilityCanBePrivate")
@OptIn(HealthCheckAPI::class)
@Serializable
data class HealthCheck(
    @kotlinx.serialization.Transient
    private val call: ApplicationCall? = null
) {
    val errors: MutableList<String> = mutableListOf()
    val server: ServerCheck = ServerCheck()
    val security: SecurityCheck = SecurityCheck()
    val database: DatabaseCheck = DatabaseManager.getHealthCheck()
    val application: ApplicationCheck = ApplicationCheck()
    val graphQL: GraphQLCheck = GraphQLCheck()
    val snowflake: SnowflakeCheck = SnowflakeCheck()
    val endpoints: List<String> = call?.application?.collectRoutes() ?: emptyList()

    init {
        errors.addAll(server.errors)
        errors.addAll(security.errors)
        errors.addAll(database.errors)
        errors.addAll(application.errors)
        errors.addAll(graphQL.errors)
        errors.addAll(snowflake.errors)

        if (endpoints.isEmpty()) {
            errors.add("No Endpoints Detected.")
        }

        if (errors.isEmpty()) {
            errors.add("No Errors Detected.")
        }
    }
}
