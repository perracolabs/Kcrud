/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config.sections

/**
 * Contains settings related to CORS.
 *
 * @property allowedHosts The list of allowed hosts used in CORS.
 */
internal data class Cors(
    val allowedHosts: List<String>
)