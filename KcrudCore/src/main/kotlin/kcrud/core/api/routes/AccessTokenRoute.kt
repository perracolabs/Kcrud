/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcrud.core.admin.env.security.authentication.AuthenticationToken
import kcrud.core.admin.env.security.user.ContextPrincipal
import kcrud.core.admin.settings.AppSettings
import kcrud.core.plugins.RateLimitScope
import kcrud.core.utils.NetworkUtils

/**
 * Access-token endpoints.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Route.accessTokenRoute() {

    val keyToken = "token"

    /**
     * Generates a new JWT token and sends it as a response.
     * If token generation fails, responds with an Internal Server Error status.
     */
    suspend fun ApplicationCall.respondWithToken() {
        try {
            val principal: ContextPrincipal = this.principal<ContextPrincipal>()
                ?: throw IllegalArgumentException("Invalid user.")
            val jwtToken: String = AuthenticationToken.generate(principal = principal)
            respond(hashMapOf(keyToken to jwtToken))
        } catch (e: IllegalArgumentException) {
            respond(status = HttpStatusCode.BadRequest, message = e.message ?: "Invalid user.")
        } catch (e: Exception) {
            respond(status = HttpStatusCode.InternalServerError, message = "Failed to generate token.")
        }
    }

    route("auth/token") {

        // Endpoint for initial token generation; requires Basic Authentication credentials.
        rateLimit(RateLimitName(name = RateLimitScope.NEW_AUTH_TOKEN.key)) {
            authenticate(AppSettings.security.basic.providerName) {
                // Creates a new token and responds with it.
                post("create") {
                    call.respondWithToken()
                }
            }
        }

        // Endpoint for token refresh.
        // No Basic Authentication is required here, but an existing token's validity will be checked.
        // For example, in Postman set the endpoint and in the Headers add an Authorization key
        // with a 'Bearer' holding a previous valid token.
        post("refresh") {
            val tokenState: AuthenticationToken.TokenState = AuthenticationToken.getState(call)

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
                    call.respond(status = HttpStatusCode.Unauthorized, message = "Invalid token.")
                }
            }
        }

        // Dump the endpoints to the console.
        NetworkUtils.logEndpoints(
            reason = "Authentication Tokens endpoints. Requires an authorization header",
            endpoints = listOf("auth/token/create", "auth/token/refresh")
        )

        // Alternative example showing how to create tokens via the browser URL.
        if (AppSettings.security.basic.isEnabled) {
            authenticate(AppSettings.security.basic.providerName) {
                // Generate auth tokens via the browser URL.
                get("form-create") {
                    call.respondWithToken()
                }
            }

            // Dump the endpoints to the console.
            NetworkUtils.logEndpoints(
                reason = "Create authentication tokens via the browser URL",
                endpoints = listOf("auth/token/form-create")
            )
        }
    }
}
