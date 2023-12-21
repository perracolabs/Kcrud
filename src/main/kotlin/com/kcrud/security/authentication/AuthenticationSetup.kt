/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.settings.SettingsProvider
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Security class responsible for the configuration of the app authentication,
 * for both Basic and JWT.
 */
internal object AuthenticationSetup {

    /**
     * Configures JWT authentication.
     *
     * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
     */
    fun configureJwt(config: AuthenticationConfig) {

        config.jwt {
            realm = SettingsProvider.security.jwt.realm

            // Build and set the JWT verifier with the configured settings.
            verifier(
                JWT
                    .require(Algorithm.HMAC256(SettingsProvider.security.jwt.secretKey))
                    .withAudience(SettingsProvider.security.jwt.audience)
                    .withIssuer(SettingsProvider.security.jwt.issuer)
                    .build()
            )

            // Validate the credentials; check if the audience in the token matches the expected audience.
            validate { credential ->
                if (credential.payload.audience.contains(SettingsProvider.security.jwt.audience)) {
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

        config.basic(SettingsProvider.security.basicAuth.providerName) {
            realm = SettingsProvider.security.basicAuth.realm

            validate { credentials ->
                val localCredentials = SettingsProvider.security.basicAuth.credentials

                if (credentials.name == localCredentials.username && credentials.password == localCredentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
