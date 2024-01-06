/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.pagination

import io.ktor.server.application.*
import kcrud.base.exceptions.pagination.PaginationError
import kcrud.base.exceptions.pagination.PaginationException

/**
 * Extension function to construct a [Pageable] instance from an ApplicationRequest query parameters.
 */
fun ApplicationCall.getPageable(): Pageable? {
    val pageIndex: Int? = request.queryParameters["page"]?.toIntOrNull()
    val pageSize: Int? = request.queryParameters["size"]?.toIntOrNull()
    if ((pageIndex == null) != (pageSize == null)) {
        throw PaginationException(error = PaginationError.InvalidPageablePair)
    }

    val orderFieldName: String? = request.queryParameters["order"]
    val sortDirection: String? = request.queryParameters["sort"]
    if ((orderFieldName == null) != (sortDirection == null)) {
        throw PaginationException(error = PaginationError.InvalidOrderPair)
    }

    // If no parameters are provided, return null.
    if (pageIndex == null && orderFieldName == null) {
        return null
    }

    // Create the Order object if ordering parameters are provided.
    val order = orderFieldName?.let {
        val direction = Pageable.SortDirection.entries.firstOrNull { direction ->
            direction.name.equals(sortDirection, ignoreCase = true)
        } ?: throw PaginationException(error = PaginationError.InvalidOrderDirection, reason = sortDirection)

        Pageable.Order(field = orderFieldName, sort = direction)
    }

    return Pageable(
        page = pageIndex ?: 1,
        size = pageSize ?: 0,
        order = order
    )
}
