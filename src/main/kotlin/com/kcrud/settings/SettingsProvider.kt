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
object SettingsProvider {
    private lateinit var settings: AppSettings

    val get: AppSettings
        get() {
            if (!SettingsProvider::settings.isInitialized) {
                throw UninitializedPropertyAccessException(
                    "Uninitialized SettingsProvider. Call 'SettingsProvider.install()' in the application pipeline."
                )
            }
            return settings
        }

    fun install(pipeline: Application) {
        settings = AppSettings(pipeline.environment.config)
    }
}