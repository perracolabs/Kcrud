/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security

import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

/**
 * Configures the routes rate limits.
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/rate-limit.html)
 */
class RateLimitSetup {

    fun configure(rateLimitConfig: RateLimitConfig) {
        rateLimitConfig.apply {
            register(RateLimitName(SCOPE_NEW_TOKEN)) {
                // Example limiting new tokens generation at 100 every 10 second.
                rateLimiter(limit = 100, refillPeriod = 10.seconds)
            }

            register(RateLimitName(SCOPE_PUBLIC)) {
                // Example limiting public API calls at 1000 per second.
                rateLimiter(limit = 1_000, refillPeriod = 1.seconds)
            }
        }
    }

    companion object {
        // Scope key for authorization tokens.
        const val SCOPE_NEW_TOKEN = "new_token"

        // Scope key for the public API.
        const val SCOPE_PUBLIC = "public"
    }
}