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
internal object SettingsProvider {
    @Volatile
    private lateinit var settings: AppSettings

    val server: AppSettings.Server get() = settings.server
    val deployment: AppSettings.Deployment get() = settings.deployment
    val cors: AppSettings.Cors get() = settings.cors
    val database: AppSettings.Database get() = settings.database
    val docs: AppSettings.Docs get() = settings.docs
    val graphql: AppSettings.GraphQL get() = settings.graphql
    val security: AppSettings.Security get() = settings.security

    @Synchronized
    fun load(context: Application) {
        if (::settings.isInitialized)
            return

        val tracer = Tracer<SettingsProvider>()
        tracer.debug("Loading application configuration.")

        // Maps top-level configuration paths (like "ktor.database") to specific data classes
        // (such as AppSettings.Database::class).
        // While top-level sections can be named freely, as this mapping dictates their data class
        // associations, the names for nested configuration sections must align exactly with the
        // property names in their respective nested data classes.
        val mappings = mapOf(
            "ktor" to AppSettings.Server::class,
            "ktor.deployment" to AppSettings.Deployment::class,
            "ktor.cors" to AppSettings.Cors::class,
            "ktor.database" to AppSettings.Database::class,
            "ktor.docs" to AppSettings.Docs::class,
            "ktor.graphql" to AppSettings.GraphQL::class,
            "ktor.security" to AppSettings.Security::class
        )

        @OptIn(SettingsAPI::class)
        settings = SettingsParser.parse(
            configuration = context.environment.config,
            mappings = mappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
