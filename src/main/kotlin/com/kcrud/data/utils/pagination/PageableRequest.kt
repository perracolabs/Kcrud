/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.utils.pagination

import io.ktor.server.application.*

/**
 * Extension function to construct a [Pageable] instance from an ApplicationRequest query parameters.
 */
internal fun ApplicationCall.getPageable(): Pageable? {
    val pageIndex: Int? = request.queryParameters["page"]?.toIntOrNull()
    val pageSize: Int? = request.queryParameters["size"]?.toIntOrNull()
    val sortFieldName: String? = request.queryParameters["sort"]
    val sortDirection: String? = request.queryParameters["direction"]

    // Check if pagination and sorting parameters are valid pairs.
    val isPaginationValid = (pageIndex != null && pageSize != null) || (pageIndex == null && pageSize == null)
    val isSortingValid = (sortFieldName != null && sortDirection != null) || (sortFieldName == null && sortDirection == null)

    // If neither pagination nor sorting parameters are provided, return null.
    if (pageIndex == null && pageSize == null && sortFieldName == null && sortDirection == null) {
        return null
    }

    // Ensure that incomplete pairs are not provided.
    require(isPaginationValid) { "Incomplete pageable attributes. Either both page and size must be specified, or none of them." }
    require(isSortingValid) { "Incomplete sorting attributes. Either both sort and direction must be specified, or none of them." }

    // Create the Sort object if sorting parameters are provided
    val sort = if (sortFieldName != null) {
        Pageable.Sort(
            field = sortFieldName,
            direction = Pageable.SortDirection.entries.firstOrNull { it.name.equals(sortDirection, ignoreCase = true) }
                ?: throw IllegalArgumentException("Sort direction not found: $sortDirection")
        )
    } else null

    return Pageable(
        page = pageIndex ?: 1,
        size = pageSize ?: 0,
        sort = sort
    )
}
