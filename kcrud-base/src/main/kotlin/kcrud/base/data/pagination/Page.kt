/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.pagination

import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * Holds the data for a page of results.
 *
 * @param content The data that forms the content in a page.
 * @param info Information about the current page and the entire dataset.
 */
@Serializable
open class Page<out T : Any>(
    val content: List<T>,
    val info: Info
) {
    /**
     * Information about the current page and the entire dataset.
     *
     * @param totalPages The total number of pages available based on the pagination settings.
     * @param pageIndex The current page number (usually starting from 1).
     * @param totalElements Total number of elements in the entire dataset, not just a page.
     * @param elementsPerPage The number of elements per each page.
     * @param elementsInPage The number of elements in the current page.
     */
    @Serializable
    data class Info(
        val totalPages: Int,
        val pageIndex: Int,
        val totalElements: Int,
        val elementsPerPage: Int,
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
         * Factory method to create a new [Page] object.
         *
         * @param content The list of objects for the current page.
         * @param totalElements The total number of elements in the entire dataset, not just the page.
         * @param pageable The pagination information that was used to request the content, or null if none was used.
         * @return A new [Page] object with the given content, including a computed page [Info] details.
         */
        fun <T : Any> build(content: List<T>, totalElements: Long, pageable: Pageable?): Page<T> {
            val (totalPages, pageSize) = pageable?.let { pageableInstance ->
                // If the page size is 0, then the requested size is the entire dataset elements.
                val requestedSize = if (pageableInstance.size == 0) content.size else pageableInstance.size
                val totalPages = max((totalElements + pageableInstance.size - 1) / requestedSize, 1)
                totalPages to requestedSize
            } ?: (1 to content.size) // If no pagination was requested, then is 1 page with the entire dataset.

            val pageIndex = pageable?.page ?: 1

            return Page(
                content = content,
                info = Info(
                    totalPages = totalPages.toInt(),
                    pageIndex = pageIndex,
                    totalElements = totalElements.toInt(),
                    elementsPerPage = pageSize,
                    elementsInPage = content.size
                )
            )
        }
    }
}


