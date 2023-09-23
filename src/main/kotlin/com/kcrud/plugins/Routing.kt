/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.routes.EmployeeRoutes
import com.kcrud.routes.RootRoutes
import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
        RootRoutes().root(this)

        // JWT Authentication for employee-related routes.
        if (SettingsProvider.get.jwt.isEnabled) {
            authenticate {
                EmployeeRoutes().employee(this)
            }
        } else {
            // No authentication required.
            EmployeeRoutes().employee(this)
        }
    }
}
