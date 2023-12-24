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
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * Extension function to apply pagination to a database [Query] based on the provided [Pageable] object.
 *
 * If Pageable is not null, then first applies the chosen sort-order (if defined in pageable),
 * and after it calculates the start index based on the page and size to finally apply it
 * as a limit to the query.
 *
 * If the page size is 0, then no limit is applied.
 *
 * If [Pageable] is null, the original query is returned without pagination.
 *
 * @param pageable An optional [Pageable] object containing pagination information.
 * @return The Query with pagination applied if Pageable is provided, otherwise the original Query.
 */
internal fun Query.applyPagination(pageable: Pageable?): Query {
    pageable?.let {
        QuerySortingHelper.applyOrder(query = this, pageable = pageable)

        if (it.pageSize > 0) {
            val startIndex = (it.pageIndex - 1) * it.pageSize
            this.limit(n = it.pageSize, offset = startIndex.toLong())
        }
    }

    return this
}

/**
 * Handles the determination and application of column-based sorting for database
 * queries according to the provided Pageable object.
 *
 * Reflection is used to dynamically resolve column references from field names
 * and employing LRU caching to optimize this process reducing the overhead
 * of repetitive reflection.
 *
 * It also ensures correct column resolution across different tables, for better
 * handling identical names in different tables.
 */
private object QuerySortingHelper {
    // Define the maximum size for the cache.
    private const val MAX_CACHE_SIZE = 100

    // LRU cache storing column references with table class and column name as the key.
    private val columnCache = object : LinkedHashMap<Pair<KClass<*>, String>, Column<*>>(MAX_CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<Pair<KClass<*>, String>, Column<*>>): Boolean {
            return size > MAX_CACHE_SIZE
        }
    }

    /**
     * Applies ordering to a query based on the provided Pageable object.
     * Sorts the query according to the field and direction specified in Pageable.
     *
     * @param query The query to apply sorting to.
     * @param pageable An optional Pageable object containing sorting information.
     */
    fun applyOrder(query: Query, pageable: Pageable?) {
        pageable?.sort?.let { sort ->
            val column = getOrderColumn(targets = query.targets, fieldName = sort.fieldName)
            val sortOrder = if (sort.direction == Pageable.SortDirection.ASC) SortOrder.ASC else SortOrder.DESC
            query.orderBy(column to sortOrder)
        }
    }

    /**
     * Iterates over a list of tables to find a column matching the specified field name.
     *
     * @param targets A list of tables to search for the column.
     * @param fieldName The name of the field representing the column.
     * @return The found Column reference.
     * @throws IllegalArgumentException If the column is not found in any of the tables.
     */
    private fun getOrderColumn(targets: List<Table>, fieldName: String): Column<*> {
        for (table in targets) {
            try {
                return findColumn(table, fieldName)
            } catch (e: IllegalArgumentException) {
                // Ignore and try the next table.
            }
        }

        throw IllegalArgumentException("Invalid sorting column: $fieldName")
    }

    /**
     * Retrieves a column reference for a given table and field name.
     * Uses cached data or performs reflection if not already cached.
     *
     * @param table The table to search for the column.
     * @param fieldName The name of the field representing the column.
     * @return The Column reference from the table.
     * @throws IllegalArgumentException If the column is not found in the table.
     */
    private fun findColumn(table: Table, fieldName: String): Column<*> {
        val tableClass = table::class
        val cacheKey = tableClass to fieldName.lowercase()

        // Retrieve from cache, or use reflection to find the column and cache it.
        return columnCache.getOrPut(cacheKey) {
            tableClass.memberProperties
                .firstOrNull {
                    it.name.equals(fieldName, ignoreCase = true) &&
                            it.returnType.classifier == Column::class
                }
                ?.getter?.call(table) as? Column<*>
                ?: throw IllegalArgumentException("Invalid sorting column: $fieldName")
        }
    }
}
