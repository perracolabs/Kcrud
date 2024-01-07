/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.api.routes.accessTokenRoute
import kcrud.base.api.routes.documentationRoute
import kcrud.base.api.routes.systemRoute
import kcrud.base.plugins.RateLimitScope
import kcrud.server.api.routes.domain.employee.employeeRoute
import kcrud.server.api.routes.domain.employment.employmentRoute
import kcrud.server.api.routes.home.rootRoute
import kotlinx.serialization.json.Json

/**
 * Initializes and sets up routing for the application.
 *
 * This includes a basic GET route for the root URL and conditional
 * JWT authentication for employee-related routes.
 *
 * Routing is the core Ktor plugin for handling incoming requests in a server application.
 * When the client makes a request to a specific URL (for example, /hello), the routing
 * mechanism allows us to define how we want this request to be served.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 *
 * See [Application Structure](https://ktor.io/docs/structuring-applications.html) for examples
 * of how to organize routes in diverse ways.
 *
 * See: [Content negotiation and serialization](https://ktor.io/docs/serialization.html#0)
 *
 * See: [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/rate-limit.html)
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
