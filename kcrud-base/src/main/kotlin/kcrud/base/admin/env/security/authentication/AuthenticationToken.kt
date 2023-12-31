/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.env.security.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kcrud.base.admin.env.security.user.ContextPrincipal
import kcrud.base.admin.env.security.user.ContextUser
import kcrud.base.admin.settings.AppSettings
import kcrud.base.admin.settings.config.sections.security.sections.JwtSettings
import kcrud.base.utils.Tracer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.*

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
object AuthenticationToken {
    private val tracer = Tracer<AuthenticationToken>()

    enum class TokenState {
        Valid, Expired, Invalid
    }

    /**
     * Verify the authorization token from the headers.
     * If invalid then the Unauthorized response is sent.
     */
    fun verify(call: ApplicationCall) {
        val tokenState: TokenState = getState(call)
        when (tokenState) {
            TokenState.Valid -> {
                // Token is valid; continue processing.
            }

            TokenState.Expired -> {
                runBlocking {
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Token has expired.")
                }
            }

            TokenState.Invalid -> {
                runBlocking {
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Token is invalid.")
                }
            }
        }
    }

    /**
     * Returns the current [TokenState] from the header authorization token.
     */
    fun getState(call: ApplicationCall): TokenState {
        return try {
            val token: String = fromHeader(call)
            val algorithm: Algorithm = Algorithm.HMAC256(AppSettings.security.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(JWT.decode(token))
            TokenState.Valid
        } catch (e: TokenExpiredException) {
            TokenState.Expired
        } catch (e: Exception) {
            tracer.error("Token verification failed: ${e.message}")
            TokenState.Invalid
        }
    }

    /**
     * Returns the current authorization token from the headers.
     */
    fun fromHeader(call: ApplicationCall): String {
        val authHeader: String? = call.request.headers.entries().find {
            it.key.equals(HttpHeaders.Authorization, ignoreCase = true)
        }?.value?.get(0)

        if (authHeader.isNullOrBlank() || !authHeader.startsWith(AuthScheme.Bearer, ignoreCase = true)) {
            throw IllegalArgumentException("Invalid Authorization header format.")
        }

        return authHeader.substring(AuthScheme.Bearer.length).trim()
    }

    /**
     * Generate a new authorization token.
     *
     * @param principal The [ContextPrincipal] details to embed in the token.
     * @return The generated JWT token.
     */
    fun generate(principal: ContextPrincipal): String {
        val jwtSettings: JwtSettings = AppSettings.security.jwt
        val tokenLifetimeMs: Long = jwtSettings.tokenLifetime
        val expirationDate = Date(System.currentTimeMillis() + tokenLifetimeMs)
        val userJson = Json.encodeToString(serializer = ContextUser.serializer(), value = principal.user)

        tracer.debug("Generating new authorization token. Expiration: $expirationDate.")

        return JWT.create()
            .withClaim(ContextUser.KEY_USER, userJson)
            .withAudience(jwtSettings.audience)
            .withIssuer(jwtSettings.issuer)
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(jwtSettings.secretKey))
    }
}
