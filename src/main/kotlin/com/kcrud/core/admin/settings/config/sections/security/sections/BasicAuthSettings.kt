/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.admin.settings.config.sections.security.sections

import com.kcrud.core.admin.settings.config.sections.security.SecuritySettings

/**
 * Basic authentication settings.
 *
 * @property isEnabled Flag to enable/disable basic authentication.
 * @property providerName Name of the basic authentication provider.
 * @property realm Security realm for the basic authentication.
 * @property customLoginForm Whether to use a custom Login Form, or the browser-based built-in one.
 * @property credentials Credentials for the basic authentication.
 */
internal data class BasicAuthSettings(
    val isEnabled: Boolean,
    val providerName: String,
    val realm: String,
    val customLoginForm: Boolean,
    val credentials: Credentials
) {
    data class Credentials(
        val username: String,
        val password: String
    ) {
        init {
            require(username.isNotBlank() && (username.length >= SecuritySettings.MIN_USERNAME_LENGTH)) {
                "Invalid credential username. Must be >= ${SecuritySettings.MIN_USERNAME_LENGTH} characters long."
            }
            require(password.isNotBlank() && (password.length >= SecuritySettings.MIN_KEY_LENGTH)) {
                "Invalid credential password. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
            }
        }
    }
}
