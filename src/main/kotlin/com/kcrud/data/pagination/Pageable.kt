/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.pagination

import com.expediagroup.graphql.generator.execution.OptionalInput

/**
 * Input parameters for pagination.
 *
 * @property page The zero-based page index.
 * @property size The size of the page to be returned.
 */
data class Pageable(
    val page: Int,
    val size: Int
) {
    init {
        require(page > 0) { "Page index must be >= 1." }
        require(size > 0) { "Page size must be > 0." }
    }

    companion object {
        fun fromOptionalPageable(pageable: OptionalInput<Pageable>): Pageable? {
            return when (pageable) {
                is OptionalInput.Defined<Pageable> -> pageable.value
                is OptionalInput.Undefined -> null
            }
        }
    }
}
