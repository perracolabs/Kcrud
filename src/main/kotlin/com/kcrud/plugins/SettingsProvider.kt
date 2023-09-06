package com.kcrud.plugins

import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*

/**
 * Initializes the configuration settings to be accessible throughout the application.
 *
 * This should be called before any other plugin.
 */
fun Application.configureSettingsProvider() {
    SettingsProvider.install(this)
}
