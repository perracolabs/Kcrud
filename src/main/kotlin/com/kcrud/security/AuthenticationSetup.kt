/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.utils.SettingsProvider
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Security class responsible for the configuration of the app authentication,
 * for both Basic and JWT.
 */
object AuthenticationSetup {

    /**
     * Configures JWT authentication.
     *
     * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
     */
    fun configureJwt(config: AuthenticationConfig) {
        val appSettings = SettingsProvider.get

        config.jwt {
            realm = appSettings.jwt.realm

            // Build and set the JWT verifier with the configured settings.
            verifier(
                JWT
                    .require(Algorithm.HMAC256(appSettings.jwt.secretKey))
                    .withAudience(appSettings.jwt.audience)
                    .withIssuer(appSettings.jwt.issuer)
                    .build()
            )

            // Validate the credentials; check if the audience in the token matches the expected audience.
            validate { credential ->
                if (credential.payload.audience.contains(appSettings.jwt.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Configures basic authentication.
     *
     * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
     */
    fun configureBasicAuth(config: AuthenticationConfig) {
        val appSettings = SettingsProvider.get

        config.basic(appSettings.basicAuth.providerName) {
            realm = appSettings.basicAuth.realm

            validate { credentials ->
                if (credentials.name == appSettings.basicAuth.username && credentials.password == appSettings.basicAuth.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
