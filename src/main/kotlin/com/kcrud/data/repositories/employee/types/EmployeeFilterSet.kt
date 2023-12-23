/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee.types

import com.kcrud.data.entities.employee.types.Honorific
import com.kcrud.data.entities.employee.types.MaritalStatus

/**
 * A set of filters that can be applied to an employee query.
 *
 * @param firstName The first name of the employee.
 * @param lastName The last name of the employee.
 * @param honorific The honorific of the employee.
 * @param maritalStatus The marital status of the employee.
 */
data class EmployeeFilterSet(
    val firstName: String? = null,
    val lastName: String? = null,
    val honorific: List<Honorific>? = null,
    val maritalStatus: List<MaritalStatus>? = null
)