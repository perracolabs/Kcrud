package com.kcrud.plugins

import com.kcrud.utils.Security
import io.ktor.server.application.*
import io.ktor.server.auth.*


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
}
