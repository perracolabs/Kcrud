/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.security.RateLimitSetup
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*

/**
 * Configure the application rate limits.
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/rate-limit.html)
 */
fun Application.configureRateLimit() {
    install(RateLimit) {
        RateLimitSetup().configure(this)
    }
}
