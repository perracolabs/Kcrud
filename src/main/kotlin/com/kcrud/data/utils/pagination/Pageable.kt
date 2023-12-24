/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.utils.pagination

/**
 * Input parameters for pagination.
 *
 * @property pageIndex The 1-based page index.
 * @property pageSize The size of the page to be returned. 0 means all elements.
 */
data class Pageable(
    val pageIndex: Int,
    val pageSize: Int,
    val sort: Sort? = null
) {
    init {
        require(pageIndex > 0) { "Page index must be >= 1." }
        require(pageSize >= 0) { "Page size must be >= 0. (0 means all elements)." }
    }

    enum class SortDirection {
        ASC, DESC
    }

    data class Sort(
        val fieldName: String,
        val direction: SortDirection
    )
}
