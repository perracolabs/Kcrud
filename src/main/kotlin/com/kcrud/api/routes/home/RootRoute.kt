/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.routes.home

import com.kcrud.admin.env.security.user.ContextPrincipal
import com.kcrud.admin.settings.AppSettings
import com.kcrud.api.views.SimpleLogin
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Root endpoints.
 */
fun Route.rootRoute() {
    if (AppSettings.security.basic.isEnabled) {
        // Basic Authentication for the root endpoint.
        if (AppSettings.security.basic.customLoginForm) {

            // Use the custom login form to handle authentication.
            get("/") { SimpleLogin.showLoginForm(call) }
            post(SimpleLogin.LOGIN_PATH) { SimpleLogin.manageResponse(call) }

        } else {
            // Use built-in browser-based basic authentication.
            authenticate(AppSettings.security.basic.providerName) {
                get("/") {
                    val principal = call.principal<ContextPrincipal>()
                    call.respondText("Authentication successful. Welcome ${principal?.user?.id}!")
                }
            }
        }
    } else {
        // No authentication required.
        get("/") { call.respondText("Hello World!") }
    }
}
