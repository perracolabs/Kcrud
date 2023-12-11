/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.security.AuthenticationToken
import com.kcrud.security.RateLimitSetup
import com.kcrud.settings.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Access-token endpoints.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Route.accessTokenRouting() {

    val authTokenOath = "auth/token"
    val keyToken = "token"

    /**
     * Generates a new JWT token and sends it as a response.
     * If token generation fails, responds with an Internal Server Error status.
     */
    suspend fun ApplicationCall.respondWithToken() {
        try {
            val jwtToken = AuthenticationToken.generate()
            respond(hashMapOf(keyToken to jwtToken))
        } catch (e: Exception) {
            respond(HttpStatusCode.InternalServerError, "Failed to generate token.")
        }
    }

    route(authTokenOath) {

        // Endpoint for initial token generation; requires Basic Authentication.
        rateLimit(RateLimitName(RateLimitSetup.Scope.NEW_AUTH_TOKEN.key)) {
            authenticate(SettingsProvider.get.security.basicAuth.providerName) {
                post("create") {
                    call.respondWithToken()
                }
            }
        }

        // Endpoint for token refresh.
        // No Basic Authentication is required here, but an existing token's validity will be checked.
        post("refresh") {
            val tokenState = AuthenticationToken.getState(call)

            when (tokenState) {
                AuthenticationToken.TokenState.Valid -> {
                    // Token is still valid; return the same token to the client.
                    val jwtToken = AuthenticationToken.fromHeader(call)
                    call.respond(hashMapOf(keyToken to jwtToken))
                }

                AuthenticationToken.TokenState.Expired -> {
                    // Token has expired; generate a new token and respond with it.
                    call.respondWithToken()
                }

                AuthenticationToken.TokenState.Invalid -> {
                    // Token is invalid; respond with an Unauthorized status.
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
                }
            }
        }
    }
}
