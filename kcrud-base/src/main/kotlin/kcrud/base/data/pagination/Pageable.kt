/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.pagination

/**
 * Input parameters for pagination.
 *
 * @property page The 1-based page index.
 * @property size The size of the page to be returned. 0 means all elements.
 * @property order The optional order to apply to the results.
 */
data class Pageable(
    val page: Int,
    val size: Int,
    val order: Order? = null
) {
    init {
        require(page > 0) { "Page index must be >= 1." }
        require(size >= 0) { "Page size must be >= 0. (0 means all elements)." }
    }

    enum class SortDirection {
        ASC, DESC
    }

    data class Order(
        val field: String,
        val sort: SortDirection
    )
}
