/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes.main

import com.kcrud.utils.NetworkUtils
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

/**
 * Defines the health check endpoint for the application.
 *
 * This endpoint ("/health") provides a simple way to monitor the operational status
 * of the application.
 *
 * The current implementation checks the basic readiness of the application. Future
 * enhancements could include more complex health checks, like database connectivity,
 * external service availability, or other critical component checks.
 */
fun Route.healthCheckRoute() {
    route("/health") {
        get {
            val healthCheckResponse = HealthCheckResponse(ready = true)
            call.respond(healthCheckResponse)
        }
    }

    NetworkUtils.logEndpoint(
        reason = "Health Check",
        endpoint = "health"
    )
}

@Serializable
data class HealthCheckResponse(
    val ready: Boolean,
    val timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
)
