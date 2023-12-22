/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.entities.employee.types.Honorific
import com.kcrud.data.entities.employee.types.MaritalStatus

data class EmployeeFilterSet(
    val firstName: String? = null,
    val lastName: String? = null,
    val honorific: List<Honorific>? = null,
    val maritalStatus: List<MaritalStatus>? = null
)
