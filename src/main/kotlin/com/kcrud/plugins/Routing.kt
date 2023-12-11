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
import com.kcrud.security.RateLimitSetup
import com.kcrud.settings.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

/**
 * Initializes and sets up routing for the application.
 *
 * This includes a basic GET route for the root URL and conditional
 * JWT authentication for employee-related routes.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureRouting() {

    routing {
        rateLimit(RateLimitName(RateLimitSetup.Scope.PUBLIC_API.key)) {
            rootRouting()
            employeeRouting()
            employmentRouting()
        }

        accessTokenRouting()

        // Swagger UI.
        // URL: http://localhost:8080/swagger
        // WIth JetBrains Ultimate Edition, the documentation can be auto-generated following the next steps:
        // 1. Place the caret over the 'routing' instruction defined above at the start of this function.
        // 2. Press Alt+Enter, and select 'Generate Swagger/OpenAPI Documentation'.
        if (SettingsProvider.get.deployment.swagger) {
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") { version = "4.15.5" }
            openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") { codegen = StaticHtmlCodegen() }
        }
    }
}
