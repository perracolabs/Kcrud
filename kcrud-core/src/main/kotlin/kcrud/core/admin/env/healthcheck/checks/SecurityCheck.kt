/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.admin.env.healthcheck.checks

import kcrud.core.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.core.admin.settings.AppSettings
import kotlinx.serialization.Serializable

@Suppress("unused")
@HealthCheckAPI
@Serializable
data class SecurityCheck(
    val errors: MutableList<String> = mutableListOf(),
    val jwtEnabled: Boolean = AppSettings.security.jwt.isEnabled,
    val basicAuthEnabled: Boolean = AppSettings.security.basic.isEnabled,
    val publicApiRateLimit: Int = AppSettings.security.constraints.publicApi.limit,
    val publicApiRateRefillMs: Long = AppSettings.security.constraints.publicApi.refill,
    val newTokenRateLimit: Int = AppSettings.security.constraints.newToken.limit,
    val newTokenRateRefillMs: Long = AppSettings.security.constraints.newToken.refill,
) {
    init {
        val className = this::class.simpleName

        if (!jwtEnabled && !basicAuthEnabled) {
            errors.add("$className. No security mechanism is enabled.")
        }
    }
}
