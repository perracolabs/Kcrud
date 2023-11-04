/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security

import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

/**
 * Configures the routes rate limits by defining the maximum allowed calls per period.
 *
 * These must be applied when defining the routes by using the same scope key. Example:
 *
 *```
 * routing {
 *      rateLimit(RateLimitName(RateLimitSetup.SCOPE_PUBLIC)) {
 *           get("some_endpoint") { ... }
 *      }
 * }
 *```
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/rate-limit.html)
 */
class RateLimitSetup {

    enum class Scope(val key: String) {
        NEW_AUTH_TOKEN("new_auth_token"), // Scope key for authorization tokens.
        PUBLIC_API("public_api") // Scope key for the public API.
    }

    fun configure(rateLimitConfig: RateLimitConfig) {
        rateLimitConfig.apply {

            // Example scope for new token generation rate limit.
            register(RateLimitName(Scope.NEW_AUTH_TOKEN.key)) {
                rateLimiter(limit = 100, refillPeriod = 10.seconds)
            }

            // Example scope for the public API rate limit.
            register(RateLimitName(Scope.PUBLIC_API.key)) {
                rateLimiter(limit = 1_000, refillPeriod = 1.seconds)
            }
        }
    }
}