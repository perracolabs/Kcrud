/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.shared

import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.pagination.Page

/**
 * Paginated employee for GraphQL queries.
 *
 * @param content The data that forms the content in a page.
 * @param pageInfo Information about the current page and the entire dataset.
 */
class EmployeeConnection(
    val content: List<Employee>,
    val pageInfo: Page.PageInfo
)
