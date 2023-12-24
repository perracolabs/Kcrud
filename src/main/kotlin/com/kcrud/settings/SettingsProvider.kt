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
    private lateinit var settings: AppSettings

    val global: AppSettings.Global get() = getSettings().global
    val deployment: AppSettings.Deployment get() = getSettings().deployment
    val cors: AppSettings.Cors get() = getSettings().cors
    val database: AppSettings.Database get() = getSettings().database
    val docs: AppSettings.Docs get() = getSettings().docs
    val graphql: AppSettings.GraphQL get() = getSettings().graphql
    val security: AppSettings.Security get() = getSettings().security

    fun configure(context: Application) {
        settings = AppSettings(context.environment.config)
    }

    private fun getSettings(): AppSettings {
        if (!::settings.isInitialized) {
            throw UninitializedPropertyAccessException(
                "Uninitialized SettingsProvider. Call 'SettingsProvider.install()' in the application pipeline."
            )
        }
        return settings
    }
}