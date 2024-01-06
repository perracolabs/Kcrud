/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import kcrud.base.admin.env.security.authentication.AuthenticationSetup

/**
 * Configures the Basic and JWT-based authentications.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Application.configureAuthentication() {

    authentication {
        AuthenticationSetup.configureJwt(config = this)
        AuthenticationSetup.configureBasicAuth(config = this)
    }
}
