/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import com.kcrud.system.Tracer
import io.ktor.server.application.*

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
internal object AppSettings {
    @Volatile
    private lateinit var config: Config

    val server: Config.Server get() = config.server
    val deployment: Config.Deployment get() = config.deployment
    val cors: Config.Cors get() = config.cors
    val database: Config.Database get() = config.database
    val docs: Config.Docs get() = config.docs
    val graphql: Config.GraphQL get() = config.graphql
    val security: Config.Security get() = config.security

    @Synchronized
    fun load(context: Application) {
        if (::config.isInitialized)
            return

        val tracer = Tracer<AppSettings>()
        tracer.debug("Loading application configuration.")

        // Maps top-level configuration paths (like "ktor.database") to specific data classes
        // (such as AppSettings.Database::class).
        // While top-level sections can be named freely, as this mapping dictates their data class
        // associations, the names for nested configuration sections must align exactly with the
        // property names in their respective nested data classes.
        val mappings = mapOf(
            "ktor" to Config.Server::class,
            "ktor.deployment" to Config.Deployment::class,
            "ktor.cors" to Config.Cors::class,
            "ktor.database" to Config.Database::class,
            "ktor.docs" to Config.Docs::class,
            "ktor.graphql" to Config.GraphQL::class,
            "ktor.security" to Config.Security::class
        )

        @OptIn(SettingsAPI::class)
        config = ConfigParser.parse(
            configuration = context.environment.config,
            mappings = mappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
