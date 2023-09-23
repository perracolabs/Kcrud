/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.controllers

import com.kcrud.utils.Security
import com.kcrud.utils.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Handles the routes for generating and refreshing authorization tokens.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
class TokenRoutes {
    /**
     * Endpoint for initial token generation; requires Basic Authentication.
     */
    fun generateTokenEndpoint(route: Route) {
        route.authenticate(SettingsProvider.get.basicAuth.providerName) {
            post("create") {
                val jwtToken = Security.generateToken()
                call.respond(hashMapOf("token" to jwtToken))
            }
        }
    }

    /**
     * Endpoint for token refresh.
     * No Basic Authentication is required here, but an existing token's validity will be checked.
     */
    fun refreshTokenEndpoint(route: Route) {
        route.post("refresh") {
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
}
