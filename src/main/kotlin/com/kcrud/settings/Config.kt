/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import com.kcrud.data.database.service.DatabaseManager
import com.kcrud.graphql.utils.GraphQLFramework
import com.kcrud.utils.DeploymentType

/**
 * Represents the application's configuration settings as a structured, easily accessible model.
 *
 * For accurate mapping, the names of nested data classes must correspond directly with
 * their configuration section names. Similarly, property names within these classes
 * must exactly match the key names in their respective configuration sections.
 */
internal data class Config(
    val server: Server,
    val deployment: Deployment,
    val cors: Cors,
    val database: Database,
    val docs: Docs,
    val graphql: GraphQL,
    val security: Security
) {
    /**
     * Contains the server root-level main configuration settings.
     *
     * @property development Ktor flag indicating whether development mode is enabled.
     *                       This makes Ktor to activate development concrete features.
     *                       See: [Development Mode](https://ktor.io/docs/development-mode.html)
     * @property machineId The unique machine ID. Used for generating unique IDs for call traceability.
     */
    data class Server(
        val development: Boolean,
        val machineId: Int
    )

    /**
     * Contains settings related to how the application is deployed.
     *
     * @property type The deployment type. Not to be confused with the development mode.
     * @property port The network port the server listens on.
     * @property host The network address the server is bound to.
     * @property apiVersion The API version.
     */
    data class Deployment(
        val type: DeploymentType,
        val port: Int,
        val host: String,
        val apiVersion: String
    )

    /**
     * Contains settings related to CORS.
     *
     * @property allowedHosts The list of allowed hosts used in CORS.
     */
    data class Cors(
        val allowedHosts: List<String>
    )

    /**
     * Database related settings.
     *
     * @property mode The database mode, either in-memory or persistent.
     * @property dbType The target database type.
     * @property name The name of the database.
     * @property path The database file location.
     * @property jdbcUrl The JDBC url database connection.
     * @property jdbcDriver The JDBC driver class name.
     * @property connectionPoolSize The database connection pool size. 0 for no connection pooling.
     */
    data class Database(
        val mode: DatabaseManager.Mode,
        val dbType: DatabaseManager.DBType,
        val name: String,
        val path: String,
        val jdbcUrl: String,
        val jdbcDriver: String,
        val connectionPoolSize: Int
    )

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
     * @property dumpSchema Whether to dump the GraphQL schema.
     */
    data class GraphQL(
        val isEnabled: Boolean,
        val framework: GraphQLFramework,
        val playground: Boolean,
        val dumpSchema: Boolean
    )

    /**
     * Security class holds settings related to security.
     *
     * @property encryption Settings related to encryption.
     * @property jwt Settings related to JWT authentication.
     * @property basicAuth Settings related to basic authentication.
     */
    data class Security(
        val encryption: Encryption,
        val jwt: Jwt,
        val basicAuth: BasicAuth
    ) {
        /**
         * Encryption key settings.
         *
         * @property algorithm Algorithm used for encrypting/decrypting data.
         * @property salt Salt used for encrypting/decrypting data.
         * @property key Secret key for encrypting/decrypting data.
         */
        data class Encryption(
            val algorithm: String,
            val salt: String,
            val key: String
        ) {
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
         * @property tokenLifetime Authentication token lifetime, in milliseconds.
         * @property audience Intended recipients of the JWT.
         * @property issuer Provider that issues the JWT.
         * @property realm Security realm for the JWT authentication.
         * @property secretKey Secret key for signing the JWT.
         */
        data class Jwt(
            val isEnabled: Boolean,
            val tokenLifetime: Long,
            val audience: String,
            val issuer: String,
            val realm: String,
            val secretKey: String
        ) {
            init {
                require(tokenLifetime > 0) { "Invalid JWT token lifetime. Must be > 0." }
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
            data class Credentials(
                val username: String,
                val password: String
            ) {
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
}
