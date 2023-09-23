/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.utils.Security
import com.kcrud.utils.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Application extension function to configure the routing setting to generate
 * and refresh authorization Tokens.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureTokenGenerator() {
    routing {
        generateTokenEndpoint()

        refreshTokenEndpoint()
    }
}

/**
 * Endpoint for initial token generation; requires Basic Authentication.
 */
private fun Route.generateTokenEndpoint() {
    authenticate(SettingsProvider.get.basicAuth.providerName) {
        post("auth/token/create") {
            val jwtToken = Security.generateToken()
            call.respond(hashMapOf("token" to jwtToken))
        }
    }
}

/**
 * Endpoint for token refresh.
 * No Basic Authentication is required here, but an existing token's validity will be checked.
 */
private fun Route.refreshTokenEndpoint() {
    post("auth/token/refresh") {
        val tokenState = Security.getTokenState(call)

        when (tokenState) {
            Security.TokenState.Valid -> {
                // Token is still valid; return the same token to the client.
                val jwtToken = Security.getAuthorizationToken(call)
                call.respond(hashMapOf("token" to jwtToken))
            }

            Security.TokenState.Expired -> {
                // Token has expired; generate a new token and respond with it.
                val jwtToken = Security.generateToken()
                call.respond(hashMapOf("token" to jwtToken))
            }

            Security.TokenState.Invalid -> {
                // Token is invalid; respond with an Unauthorized status.
                call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
            }
        }
    }
}