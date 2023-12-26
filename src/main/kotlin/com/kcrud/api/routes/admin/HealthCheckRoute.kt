/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.routes.admin

import com.kcrud.admin.env.healthcheck.HealthCheck
import com.kcrud.admin.env.healthcheck.utils.collectRoutes
import com.kcrud.admin.env.security.snowflake.SnowflakeFactory
import com.kcrud.admin.settings.AppSettings
import com.kcrud.utils.NetworkUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
    authenticate(AppSettings.security.basicAuth.providerName) {
        get("/health") {
            val endpoints = call.application.collectRoutes()
            val healthCheck = HealthCheck(endpoints = endpoints)
            call.respond(healthCheck)
        }

        get("/snowflake/{id}") {
            call.respond(HttpStatusCode.OK, SnowflakeFactory.parse(id = call.parameters["id"]!!))
        }
    }

    NetworkUtils.logEndpoints(
        reason = "System endpoints",
        endpoints = listOf("health", "snowflake/${SnowflakeFactory.nextId()}")
    )
}
