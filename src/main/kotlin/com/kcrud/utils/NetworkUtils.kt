/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.settings.SettingsProvider
import org.slf4j.LoggerFactory

internal object NetworkUtils {
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun logEndpoints(reason: String? = null, endpoints: List<String>) {
        var host = SettingsProvider.get.deployment.host
        host = if (host == "0.0.0.0") "localhost" else host

        val port = SettingsProvider.get.deployment.port
        val protocol = if (port == 443) "https" else "http"
        val url = "$protocol://$host:$port"

        reason?.let { logger.info(it) }

        endpoints.forEach { endpoint ->
            logger.info("$url/$endpoint")
        }
    }
}
