/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.plugins

import com.kcrud.core.admin.settings.AppSettings
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configures the routes rate limits by defining the maximum allowed calls per period.
 *
 * These must be applied when defining the routes by using the same scope key. Example:
 *
 *```
 * routing {
 *      rateLimit(RateLimitName(RateLimitScope.PUBLIC_API)) {
 *           get("some_endpoint") { ... }
 *      }
 * }
 *```
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/rate-limit.html)
 */
fun Application.configureRateLimit() {
    install(RateLimit) {
        // Example scope for the public API rate limit.
        register(RateLimitName(name = RateLimitScope.PUBLIC_API.key)) {
            rateLimiter(
                limit = AppSettings.security.constraints.publicApi.limit,
                refillPeriod = AppSettings.security.constraints.publicApi.refill.milliseconds
            )
        }

        // Example scope for new token generation rate limit.
        register(RateLimitName(name = RateLimitScope.NEW_AUTH_TOKEN.key)) {
            rateLimiter(
                limit = AppSettings.security.constraints.newToken.limit,
                refillPeriod = AppSettings.security.constraints.newToken.refill.milliseconds
            )
        }
    }
}

/**
 * The rate limit scopes used in the application.
 */
enum class RateLimitScope(val key: String) {
    NEW_AUTH_TOKEN("new_auth_token"), // Scope key for authorization tokens.
    PUBLIC_API("public_api") // Scope key for the public API.
}
