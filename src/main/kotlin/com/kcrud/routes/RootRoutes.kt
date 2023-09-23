/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Managers the root endpoint.
 */
class RootRoutes {
    fun root(route: Route) {
        route {
            val appSettings = SettingsProvider.get

            if (appSettings.basicAuth.isEnabled) {
                // Basic Authentication for the root endpoint.
                authenticate(appSettings.basicAuth.providerName) {
                    get("/") {
                        call.respondText("Hello World! You are authenticated.")
                    }
                }
            } else {
                // No authentication required.
                get("/") {
                    call.respondText("Hello World!")
                }
            }
        }
    }
}