/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config.sections.security

/**
 * Security constraints settings.
 *
 * @property publicApi Rate limit specification for the Public API endpoints.
 * @property newToken Rate limit specification for the New Authentication Token generation endpoint.
 */
data class Constraints(
    val publicApi: LimitSpec,
    val newToken: LimitSpec
) {
    data class LimitSpec(
        val limit: Int,
        val refill: Long
    ) {
        init {
            require(limit > 0) { "Invalid rate limit. Must be > 0." }
            require(refill > 0) { "Invalid rate refill. Must be > 0." }
        }
    }
}
