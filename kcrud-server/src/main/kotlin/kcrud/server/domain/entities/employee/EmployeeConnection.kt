/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employee

import kcrud.base.data.pagination.Page
import kotlinx.serialization.Serializable

/**
 * Paginated employee, suitable for GraphQL queries.
 *
 * For REST this is not needed, as a [Page] with the [Employee] type can
 * be directly serialized.
 *
 * Graphql on the other hand would need to return this class instead,
 * as it does not support generics.
 *
 * @param page The [Page] to wrap.
 */
@Suppress("unused")
@Serializable
data class EmployeeConnection(private val page: Page<Employee>) {
    /** The data that forms the content of a page. */
    val content: List<Employee> get() = page.content

    /** Information about the current page and the entire dataset. */
    val info: Page.Info get() = page.info
}
