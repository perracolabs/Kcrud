/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import io.ktor.server.application.*

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * After initializing the application with the `install` function,
 * the [AppSettings] can be accessed anywhere throughout the application.
 */
internal object SettingsProvider {
    @Volatile
    private lateinit var settings: AppSettings

    val global: AppSettings.Global get() = settings.global
    val deployment: AppSettings.Deployment get() = settings.deployment
    val cors: AppSettings.Cors get() = settings.cors
    val database: AppSettings.Database get() = settings.database
    val docs: AppSettings.Docs get() = settings.docs
    val graphql: AppSettings.GraphQL get() = settings.graphql
    val security: AppSettings.Security get() = settings.security

    @OptIn(SettingsAPI::class)
    fun configure(context: Application) {
        if (::settings.isInitialized) {
            return
        }

        synchronized(this) {
            if (::settings.isInitialized) {
                // Double check inside synchronized block.
                return
            }

            val configMappings = mapOf(
                "ktor" to AppSettings.Global::class,
                "ktor.deployment" to AppSettings.Deployment::class,
                "ktor.cors" to AppSettings.Cors::class,
                "ktor.database" to AppSettings.Database::class,
                "ktor.docs" to AppSettings.Docs::class,
                "ktor.graphql" to AppSettings.GraphQL::class,
                "ktor.security" to AppSettings.Security::class
            )

            settings = SettingsParser.parse(
                config = context.environment.config,
                configMappings = configMappings
            )
        }
    }
}