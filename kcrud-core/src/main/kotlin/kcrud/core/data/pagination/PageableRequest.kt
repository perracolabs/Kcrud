/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.data.pagination

import io.ktor.server.application.*

/**
 * Extension function to construct a [Pageable] instance from an ApplicationRequest query parameters.
 */
fun ApplicationCall.getPageable(): Pageable? {
    val pageIndex: Int? = request.queryParameters["page"]?.toIntOrNull()
    val pageSize: Int? = request.queryParameters["size"]?.toIntOrNull()
    val orderFieldName: String? = request.queryParameters["order"]
    val sortDirection: String? = request.queryParameters["sort"]

    // Check if pagination and order parameters are valid pairs.
    val isPaginationValid = (pageIndex != null && pageSize != null) || (pageIndex == null && pageSize == null)
    val isOrderingValid = (orderFieldName != null && sortDirection != null) || (orderFieldName == null && sortDirection == null)

    // If neither pagination nor ordering parameters are provided, return null.
    if (pageIndex == null && pageSize == null && orderFieldName == null && sortDirection == null) {
        return null
    }

    // Ensure that incomplete pairs are not provided.
    require(isPaginationValid) { "Incomplete pageable attributes. Either both page and size must be specified, or none of them." }
    require(isOrderingValid) { "Incomplete order attributes. Either both order and direction must be specified, or none of them." }

    // Create the Order object if ordering parameters are provided
    val order = if (orderFieldName != null) {
        Pageable.Order(
            field = orderFieldName,
            sort = Pageable.SortDirection.entries.firstOrNull { it.name.equals(sortDirection, ignoreCase = true) }
                ?: throw IllegalArgumentException("Ordering sort direction is invalid: $sortDirection")
        )
    } else null

    return Pageable(
        page = pageIndex ?: 1,
        size = pageSize ?: 0,
        order = order
    )
}
