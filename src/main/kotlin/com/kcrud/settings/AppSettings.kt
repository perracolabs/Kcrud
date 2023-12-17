/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import com.kcrud.graphql.GraphQLFramework
import io.ktor.server.config.*

/**
 * Represents the application's global settings, as parsed from the application configuration file.
 *
 * @property global Configurations specific to main global environment.
 * @property deployment Configurations for the server's deployment settings.
 * @property security Configurations for security settings.
 */
internal data class AppSettings(
    val global: Global,
    val deployment: Deployment,
    val docs: Docs,
    val graphql: GraphQL,
    val security: Security
) {
    /**
     * Contains the main global configuration settings.
     *
     * @property development Indicates whether development mode is active.
     */
    data class Global(val development: Boolean)

    /**
     * Contains settings related to how the application is deployed.
     *
     * @property port The network port the server listens on.
     * @property host The network address the server is bound to.
     * @property apiVersion The API version.
     */
    data class Deployment(val port: Int, val host: String, val apiVersion: String)

    /**
     * Contains settings related to Swagger, OpenAPI, and Redoc.
     *
     * @property isEnabled Whether Swagger is enabled.
     * @property yamlFile The documentation location file.
     * @property swaggerPath The path to the Swagger UI.
     * @property openApiPath The path to the OpenAPI specification.
     * @property redocPath The path to the Redoc file.
     */
    data class Docs(
        val isEnabled: Boolean,
        val yamlFile: String,
        val swaggerPath: String,
        val openApiPath: String,
        val redocPath: String
    )

    /**
     * GraphQL related settings.
     *
     * @property isEnabled Whether GraphQL is enabled.
     * @property framework The GraphQL framework to use.
     * @property playground Whether to enable the GraphQL Playground.
     */
    data class GraphQL(val isEnabled: Boolean, val framework: GraphQLFramework, val playground: Boolean)

    /**
     * Security class holds settings related to security.
     *
     * @property encryption Settings related to encryption.
     * @property jwt Settings related to JWT authentication.
     * @property basicAuth Settings related to basic authentication.
     */
    data class Security(val encryption: Encryption, val jwt: Jwt, val basicAuth: BasicAuth) {

        /**
         * Encryption key settings.
         *
         * @property algorithm Algorithm used for encrypting/decrypting data.
         * @property salt Salt used for encrypting/decrypting data.
         * @property key Secret key for encrypting/decrypting data.
         */
        data class Encryption(val algorithm: String, val salt: String, val key: String) {
            init {
                require(algorithm.isNotBlank()) { "Missing encryption algorithm." }
                require(salt.isNotBlank()) { "Missing encryption salt." }
                require(key.isNotBlank() && (key.length >= MIN_KEY_LENGTH)) {
                    "Invalid encryption key. Must be >= $MIN_KEY_LENGTH characters long."
                }
            }
        }

        /**
         * JWT authentication settings.
         *
         * @property isEnabled Flag to enable/disable JWT authentication.
         * @property audience Intended recipients of the JWT.
         * @property issuer Entity that issues the JWT.
         * @property realm Security realm for the JWT authentication.
         * @property secretKey Secret key for signing the JWT.
         */
        data class Jwt(val isEnabled: Boolean, val audience: String, val issuer: String, val realm: String, val secretKey: String) {
            init {
                require(audience.isNotBlank()) { "Missing JWT audience." }
                require(issuer.isNotBlank()) { "Missing JWT issuer." }
                require(realm.isNotBlank()) { "Missing JWT realm." }
                require(secretKey.isNotBlank() && (secretKey.length >= MIN_KEY_LENGTH)) {
                    "Invalid JWT secret key. Must be >= $MIN_KEY_LENGTH characters long."
                }
            }
        }

        /**
         * Basic authentication settings.
         *
         * @property isEnabled Flag to enable/disable basic authentication.
         * @property providerName Name of the basic authentication provider.
         * @property realm Security realm for the basic authentication.
         * @property loginForm Whether to use the Login Form, or the browser-based basic authentication.
         * @property credentials Credentials for the basic authentication.
         */
        data class BasicAuth(
            val isEnabled: Boolean,
            val providerName: String,
            val realm: String,
            val loginForm: Boolean,
            val credentials: Credentials
        ) {
            data class Credentials(val username: String, val password: String) {
                init {
                    require(username.isNotBlank() && (username.length >= MIN_USERNAME_LENGTH)) {
                        "Invalid credential username. Must be >= $MIN_USERNAME_LENGTH characters long."
                    }
                    require(password.isNotBlank() && (password.length >= MIN_KEY_LENGTH)) {
                        "Invalid credential password. Must be >= $MIN_KEY_LENGTH characters long."
                    }
                }
            }
        }

        companion object {
            private const val MIN_USERNAME_LENGTH: Int = 4
            private const val MIN_KEY_LENGTH: Int = 12
        }
    }

    companion object {
        // Singleton AppSettings instance.
        private var instance: AppSettings? = null

        /**
         * Factory method to create or return the instance of AppSettings.
         *
         * @param config Configuration from which the application's settings must be parsed.
         * @return Singleton instance of AppSettings, populated with the parsed configuration data.
         */
        @OptIn(SettingsAPI::class)
        operator fun invoke(config: ApplicationConfig): AppSettings {
            return instance ?: synchronized(this) {
                instance ?: SettingsParser.parse(config = config).also { instance = it }
            }
        }
    }
}
