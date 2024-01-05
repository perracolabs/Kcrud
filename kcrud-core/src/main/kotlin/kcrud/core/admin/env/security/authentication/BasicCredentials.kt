/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.admin.env.security.authentication

import kcrud.core.admin.settings.AppSettings

object BasicCredentials {

    /**
     * Verifies the provided credentials against the configured credentials.
     * Username is case-insensitive.
     *
     * @return true if the credentials are valid, false otherwise.
     */
    fun verify(username: String?, password: String?): Boolean {
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return false
        }
        val configuredCredentials = AppSettings.security.basic.credentials
        return (username.compareTo(other = configuredCredentials.username, ignoreCase = true) == 0)
                && (password == configuredCredentials.password)
    }
}