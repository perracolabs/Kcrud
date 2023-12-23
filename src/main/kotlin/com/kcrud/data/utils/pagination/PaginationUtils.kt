/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.utils.pagination

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Query

/**
 * Extension function to apply pagination to a database [Query] based on the provided [Pageable] object.
 * If Pageable is not null, it calculates the start index based on the page and size,
 * and applies a limit to the query.
 *
 * If [Pageable] is null, the original query is returned without pagination.
 *
 * @param pageable An optional [Pageable] object containing pagination information.
 * @return The Query with pagination applied if Pageable is provided, otherwise the original Query.
 */
fun Query.applyPagination(pageable: Pageable?): Query {
    return pageable?.let {
        val startIndex = (it.page - 1) * it.size
        this.limit(n = it.size, offset = startIndex.toLong())
    } ?: this
}

/**
 * Extension function to get the [Pageable] from a request query parameters.
 */
fun ApplicationCall.getPageable(): Pageable? {
    val page: Int? = request.queryParameters["page"]?.toIntOrNull()
    val size: Int? = request.queryParameters["size"]?.toIntOrNull()

    // If both are null it means no pagination was requested.
    if (page == null && size == null)
        return null

    // If one is null and the other is not, it means the pagination request is incomplete.
    require(page != null && size != null) {
        "Incomplete pageable attributes. Either both page and size must be specified, or none of them."
    }

    return Pageable(page = page, size = size)
}
