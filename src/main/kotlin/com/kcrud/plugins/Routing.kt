/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.routes.accessTokenRouting
import com.kcrud.routes.employeeRouting
import com.kcrud.routes.employmentRouting
import com.kcrud.routes.rootRouting
import com.kcrud.settings.SettingsProvider
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
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
fun Application.routingModule() {

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
            rootRouting()
            employeeRouting()
            employmentRouting()
        }

        // Define access-token endpoints.
        accessTokenRouting()

        // Swagger UI.
        // URL: http://localhost:8080/swagger
        // WIth JetBrains Ultimate Edition, the documentation can be auto-generated following the next steps:
        // 1. Place the caret over the 'routing' instruction defined above at the start of this function.
        // 2. Press Alt+Enter, and select 'Generate Swagger/OpenAPI Documentation'.
        if (SettingsProvider.deployment.swagger) {
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") { version = "4.15.5" }
            openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") { codegen = StaticHtmlCodegen() }
        }
    }
}
