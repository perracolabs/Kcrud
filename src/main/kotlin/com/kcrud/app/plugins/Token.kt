package com.kcrud.app.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.utils.AppConfig
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
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureTokenGenerator() {

    val appConfig = AppConfig(config = environment.config)
    val oneMonthExpiration = 30 * 24 * 60 * 60 * 1000L
    val expirationDate = Date(System.currentTimeMillis() + oneMonthExpiration)

    if (appConfig.development.isEnabled) {
        routing {
            post("/token") {

                // Generate JWT token with the given settings.
                val jwtToken = JWT.create()
                    .withAudience(appConfig.jwt.audience)
                    .withIssuer(appConfig.jwt.issuer)
                    .withExpiresAt(expirationDate)
                    .sign(Algorithm.HMAC256(appConfig.jwt.secretKey))

                // Respond with the generated JWT token.
                call.respond(hashMapOf("token" to jwtToken))
            }
        }
    }
}
