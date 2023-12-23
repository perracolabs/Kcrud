/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee.types

import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.pagination.Page
import kotlinx.serialization.Serializable

/**
 * Paginated employee, suitable for GraphQL queries.
 *
 * For rest this is not needed, as a Page<Employee> can
 * directly be returned. Graphql on the other hand needs
 * would need to return this class instead, as it does not
 * support generics.
 *
 * @param content The data that forms the content in a page.
 * @param info Information about the current page and the entire dataset.
 */
@Serializable
class EmployeeSet(
    val content: List<Employee>,
    val info: Page.Info
)

