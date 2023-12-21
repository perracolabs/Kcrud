/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.snowflake

import kotlinx.datetime.LocalDateTime

/**
 * Represents the segments that compose a machine unique ID.
 */
data class SnowflakeId(
    val timestamp: LocalDateTime,
    val machineId: Int,
    val increment: Int
)
