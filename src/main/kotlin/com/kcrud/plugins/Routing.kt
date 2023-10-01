/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.routes.AccessTokenRouting
import com.kcrud.routes.EmployeeRouting
import com.kcrud.routes.EmploymentRouting
import com.kcrud.routes.RootRouting
import com.kcrud.security.RateLimitSetup
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*

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
        rateLimit(RateLimitName(RateLimitSetup.SCOPE_PUBLIC_API)) {
            RootRouting(routingNode = this).configure()
            EmployeeRouting(routingNode = this).configure()
            EmploymentRouting(routingNode = this).configure()
        }

        AccessTokenRouting(routingNode = this).configure()
    }
}
