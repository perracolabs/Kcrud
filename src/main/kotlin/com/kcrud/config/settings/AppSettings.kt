/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings

import com.kcrud.config.settings.config.Config
import com.kcrud.config.settings.config.ConfigAPI
import com.kcrud.config.settings.config.ConfigParser
import com.kcrud.config.settings.config.sections.*
import com.kcrud.utils.Tracer
import io.ktor.server.application.*

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
internal object AppSettings {
    @Volatile
    private lateinit var config: Config

    val server: Server get() = config.server
    val deployment: Deployment get() = config.deployment
    val cors: Cors get() = config.cors
    val database: Database get() = config.database
    val docs: Docs get() = config.docs
    val graphql: GraphQL get() = config.graphql
    val security: Security get() = config.security

    @Synchronized
    fun load(context: Application) {
        if (AppSettings::config.isInitialized)
            return

        val tracer = Tracer<AppSettings>()
        tracer.debug("Loading application configuration.")

        // Maps top-level configuration paths (like "ktor.database") to specific data classes
        // (such as AppSettings.Database::class).
        // While top-level sections can be named freely, as this mapping dictates their data class
        // associations, the names for nested configuration sections must align exactly with the
        // property names in their respective nested data classes.
        val mappings = mapOf(
            "ktor" to Server::class,
            "ktor.deployment" to Deployment::class,
            "ktor.cors" to Cors::class,
            "ktor.database" to Database::class,
            "ktor.docs" to Docs::class,
            "ktor.graphql" to GraphQL::class,
            "ktor.security" to Security::class
        )

        @OptIn(ConfigAPI::class)
        config = ConfigParser.parse(
            configuration = context.environment.config,
            mappings = mappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
