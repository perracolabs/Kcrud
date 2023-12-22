/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.shared

/**
 * Data pagination options.
 * @param page The page number to be retrieved.
 * @param limit The number of records to be retrieved.
 */
data class Pagination(
    val page: Int,
    val limit: Int
)
