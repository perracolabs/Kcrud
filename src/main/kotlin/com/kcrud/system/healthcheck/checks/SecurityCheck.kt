/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system.healthcheck.checks

import com.kcrud.settings.AppSettings
import kotlinx.serialization.Serializable

@Serializable
data class SecurityCheck(
    val errors: MutableList<String> = mutableListOf(),
    val jwtEnabled: Boolean = AppSettings.security.jwt.isEnabled,
    val basicAuthEnabled: Boolean = AppSettings.security.basicAuth.isEnabled
) {
    init {
        val className = this::class.simpleName

        if (!jwtEnabled && !basicAuthEnabled) {
            errors.add("$className. No security mechanism is enabled.")
        }
    }
}
