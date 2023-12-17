/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.settings.SettingsProvider
import io.ktor.server.application.*

/**
 * Initializes the configuration settings to be accessible throughout the application.
 *
 * This should be called before any other plugin.
 */
fun Application.configureSettingsProvider() {
    SettingsProvider.install(pipeline = this)
}
