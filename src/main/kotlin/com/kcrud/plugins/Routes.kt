/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.admin.settings.AppSettings
import com.kcrud.api.routes.admin.accessTokenRoute
import com.kcrud.api.routes.admin.documentationRoute
import com.kcrud.api.routes.admin.systemRoute
import com.kcrud.api.routes.domain.employeeRoute
import com.kcrud.api.routes.domain.employmentRoute
import com.kcrud.api.routes.home.rootRoute
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * Initializes and sets up routing for the application.
 *
 * This includes a basic GET route for the root URL and conditional
 * JWT authentication for employee-related routes.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 *
 * See: [Content negotiation and serialization](https://ktor.io/docs/serialization.html#0)
 *
 * See: [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)
 */
fun Application.configureRoutes() {

    routing {

        // The ContentNegotiation plugin is set at the routing level rather than
        // at the application level to prevent potential conflicts that could arise
        // if other libraries also attempt to install their own ContentNegotiation plugin,
        // which would result in a DuplicatePluginException.
        install(ContentNegotiation) {
            // Define the behavior and characteristics for JSON serialization.
            json(Json {
                prettyPrint = true         // Format JSON output for easier reading.
                encodeDefaults = true      // Serialize properties with default values.
                ignoreUnknownKeys = false  // Fail on unknown keys in the incoming JSON.
            })
        }

        // Define data endpoints.
        rateLimit(RateLimitName(name = RateLimitScope.PUBLIC_API.key)) {
            rootRoute()
            if (AppSettings.security.jwt.isEnabled) {
                authenticate {
                    employeeRoute()
                    employmentRoute()
                }
            } else {
                employeeRoute()
                employmentRoute()
            }
        }

        // Define access-token endpoints.
        accessTokenRoute()

        // Swagger UI / OpenAPI Documentation.
        documentationRoute()

        // System endpoints, (e.g. health check).
        systemRoute()
    }
}
