/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.utils.pagination

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.full.memberProperties

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
    pageable?.let {
        applySorting(query = this, pageable = pageable)

        if (it.size > 0) {
            val startIndex = (it.page - 1) * it.size
            this.limit(n = it.size, offset = startIndex.toLong())
        }
    }

    return this
}

private fun applySorting(query: Query, pageable: Pageable?) {
    pageable?.sort?.let { sort ->
        val column = getSortColumn(targets = query.targets, fieldName = sort.field)
        val sortOrder = if (sort.direction == Pageable.SortDirection.ASC) SortOrder.ASC else SortOrder.DESC
        query.orderBy(column to sortOrder)
    }
}

private fun getSortColumn(targets: List<Table>, fieldName: String): Column<*> {
    for (table in targets) {
        val property = table::class.memberProperties
            .firstOrNull { it.name.equals(fieldName, ignoreCase = true) }

        property?.let {
            val column = property.getter.call(table) as? Column<*>
            if (column != null) {
                return column
            }
        }
    }

    throw IllegalArgumentException("Invalid sorting column: $fieldName")
}
