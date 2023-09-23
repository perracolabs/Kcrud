/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.controllers.TokenRoutes
import com.kcrud.utils.Security
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

/**
 * Configures the Basic and JWT-based authentications.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Application.configureAuthentication() {

    authentication {
        Security.configureJwt(config = this)
        Security.configureBasicAuth(config = this)
    }

    val tokenRoutes = TokenRoutes()

    routing {
        route("auth/token") {
            tokenRoutes.generateTokenEndpoint(this)
            tokenRoutes.refreshTokenEndpoint(this)
        }
    }
}
