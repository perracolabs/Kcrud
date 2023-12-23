/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.pagination

import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * A page is a sublist of a list of objects.
 *
 * @param content The data that forms the content in a page.
 * @param pageInfo Information about the current page and the entire dataset.
 */
@Suppress("unused")
@Serializable
open class Page<out T : Any>(
    val content: List<T>,
    val pageInfo: PageInfo
) {
    /**
     * Information about the current page and the entire dataset.
     *
     * @param totalElements Total number of elements in the entire dataset, not just a page.
     * @param totalPages The total number of pages available based on the pagination settings.
     * @param elementsPerPage The number of elements per each page.
     * @param pageIndex The current page number (usually starting from 1).
     * @param elementsInPage The number of elements in the current page.
     */
    @Serializable
    data class PageInfo(
        val totalElements: Int,
        val totalPages: Int,
        val elementsPerPage: Int,
        val pageIndex: Int,
        val elementsInPage: Int,
    ) {
        /** True if this is the first page. */
        val isFirst: Boolean = (pageIndex == 1)

        /** True if this is the last page. */
        val isLast: Boolean = (pageIndex == totalPages)

        /** True if there is a next page. */
        val hasNext: Boolean = (pageIndex < totalPages)

        /** True if there is a previous page. */
        val hasPrevious: Boolean = (pageIndex > 1)
    }

    companion object {
        /**
         * Factory method to create a new Page object.
         *
         * @param content The list of objects for the current page.
         * @param totalElements Total number of elements in the entire dataset, not just the page.
         * @param pageable The pagination information.
         * @return A new Page object with the given content and pagination information.
         */
        fun <T : Any> create(content: List<T>, totalElements: Long, pageable: Pageable?): Page<T> {
            val totalPages = pageable?.let { max((totalElements + it.size - 1) / it.size, 1) } ?: 1
            val pageNumber = pageable?.page ?: 1
            val pageSize = pageable?.size ?: content.size

            return Page(
                content = content,
                pageInfo = PageInfo(
                    totalElements = totalElements.toInt(),
                    totalPages = totalPages.toInt(),
                    pageIndex = pageNumber,
                    elementsPerPage = pageSize,
                    elementsInPage = content.size
                )
            )
        }
    }
}


