/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.utils.pagination

import io.ktor.server.application.*

/**
 * Extension function to get the [Pageable] from a request query parameters.
 */
fun ApplicationCall.getPageable(): Pageable? {
    val page: Int? = request.queryParameters["page"]?.toIntOrNull()
    val size: Int? = request.queryParameters["size"]?.toIntOrNull()
    val sortField: String? = request.queryParameters["sort"]
    val sortDirection: String? = request.queryParameters["direction"]

    // Check if pagination and sorting parameters are valid pairs.
    val isPaginationValid = (page != null && size != null) || (page == null && size == null)
    val isSortingValid = (sortField != null && sortDirection != null) || (sortField == null && sortDirection == null)

    // If neither pagination nor sorting parameters are provided, return null.
    if (page == null && size == null && sortField == null && sortDirection == null) {
        return null
    }

    // Ensure that incomplete pairs are not provided.
    require(isPaginationValid) { "Incomplete pageable attributes. Either both page and size must be specified, or none of them." }
    require(isSortingValid) { "Incomplete sorting attributes. Either both sort and direction must be specified, or none of them." }

    // Create the Sort object if sorting parameters are provided
    val sort = if (sortField != null) {
        Pageable.Sort(
            field = sortField,
            direction = Pageable.SortDirection.entries.firstOrNull { it.name.equals(sortDirection, ignoreCase = true) }
                ?: throw IllegalArgumentException("Sort direction not found: $sortDirection")
        )
    } else null

    return Pageable(
        page = page ?: 1,
        size = size ?: 0,
        sort = sort
    )
}
