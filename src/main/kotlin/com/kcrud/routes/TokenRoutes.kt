/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.security.AuthenticationToken
import com.kcrud.security.RateLimitSetup
import com.kcrud.utils.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Handles the routes for generating and refreshing authorization tokens.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
class TokenRoutes(private val routingNode: Route) {

    fun configure() {
        routingNode.route(AUTH_TOKEN_ROUTE) {

            // Example of how to limit the rate for new token generation.
            rateLimit(RateLimitName(RateLimitSetup.SCOPE_NEW_TOKEN)) {
                setupGenerateToken(this)
            }

            setupRefreshToken(this)
        }
    }

    /**
     * Endpoint for initial token generation; requires Basic Authentication.
     */
    private fun setupGenerateToken(routeScope: Route) {
        routeScope.authenticate(SettingsProvider.get.basicAuth.providerName) {
            post("create") {
                val jwtToken = AuthenticationToken.generate()
                call.respond(hashMapOf(KEY_TOKEN to jwtToken))
            }
        }
    }

    /**
     * Endpoint for token refresh.
     * No Basic Authentication is required here, but an existing token's validity will be checked.
     */
    private fun setupRefreshToken(routeScope: Route) {
        routeScope.post("refresh") {
            val tokenState = AuthenticationToken.getState(call)

            when (tokenState) {
                AuthenticationToken.TokenState.Valid -> {
                    // Token is still valid; return the same token to the client.
                    val jwtToken = AuthenticationToken.fromHeader(call)
                    call.respond(hashMapOf(KEY_TOKEN to jwtToken))
                }

                AuthenticationToken.TokenState.Expired -> {
                    // Token has expired; generate a new token and respond with it.
                    val jwtToken = AuthenticationToken.generate()
                    call.respond(hashMapOf(KEY_TOKEN to jwtToken))
                }

                AuthenticationToken.TokenState.Invalid -> {
                    // Token is invalid; respond with an Unauthorized status.
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
                }
            }
        }
    }

    companion object {
        private const val AUTH_TOKEN_ROUTE = "auth/token"
        private const val KEY_TOKEN = "token"
    }
}
