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
 * @param content The data that forms the content in a page.
 * @param info Information about the current page and the entire dataset.
 */
@Serializable
class EmployeeConnection(
    val content: List<Employee>,
    val info: Page.Info
)
