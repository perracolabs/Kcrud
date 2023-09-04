package com.kcrud.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.utils.appSettings
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


/**
 * Application extension function to configure the routing setting to generate Tokens.
 *
 * Available only if the application is running in development mode.
 *
 * Hardcoding expiration to 1 month, tweak as needed.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureTokenGenerator() {

    val settings = appSettings()

    if (settings.global.development) {
        val oneMonthExpiration = 30 * 24 * 60 * 60 * 1000L
        val expirationDate = Date(System.currentTimeMillis() + oneMonthExpiration)

        routing {
            post("/token") {

                // Generate JWT token with the given settings.
                val jwtToken = JWT.create()
                    .withAudience(settings.jwt.audience)
                    .withIssuer(settings.jwt.issuer)
                    .withExpiresAt(expirationDate)
                    .sign(Algorithm.HMAC256(settings.jwt.secretKey))

                // Respond with the generated JWT token.
                call.respond(hashMapOf("token" to jwtToken))
            }
        }
    }
}
