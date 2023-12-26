/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config.sections.security

import com.kcrud.config.settings.config.sections.Security

/**
 * Basic authentication settings.
 *
 * @property isEnabled Flag to enable/disable basic authentication.
 * @property providerName Name of the basic authentication provider.
 * @property realm Security realm for the basic authentication.
 * @property loginForm Whether to use the Login Form, or the browser-based basic authentication.
 * @property credentials Credentials for the basic authentication.
 */
internal data class BasicAuth(
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
            require(username.isNotBlank() && (username.length >= Security.MIN_USERNAME_LENGTH)) {
                "Invalid credential username. Must be >= ${Security.MIN_USERNAME_LENGTH} characters long."
            }
            require(password.isNotBlank() && (password.length >= Security.MIN_KEY_LENGTH)) {
                "Invalid credential password. Must be >= ${Security.MIN_KEY_LENGTH} characters long."
            }
        }
    }
}
