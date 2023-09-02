package com.kcrud.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.app.AppSettings
import io.ktor.server.application.*

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
class Security {

    /**
     * Verify the JWT token from the incoming request.
     *
     * @param call The ApplicationCall from the incoming request.
     * @throws UnauthorizedException If token verification fails.
     */
    fun verifyToken(call: ApplicationCall) {
        val settings = AppSettings(config = call.application.environment.config)

        if (!settings.jwt.isEnabled)
            return

        try {
            val algorithm: Algorithm = Algorithm.HMAC256(settings.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()

            val authHeader = call.request.headers["Authorization"] ?: ""
            val token = authHeader.split("Bearer ").last()
            verifier.verify(JWT.decode(token))
        } catch (e: Exception) {
            println(e.message)
            throw UnauthorizedException("Error while authorizing.")
        }
    }
}

/**
 * Custom exception to handle unauthorized access.
 *
 * @param message The exception message.
 */
class UnauthorizedException(message: String) : RuntimeException(message)

