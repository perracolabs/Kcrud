/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employee

import com.kcrud.data.models.contact.ContactParams
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employee.
 *
 * @property firstName The first name of the employee. Must not be blank.
 * @property lastName The last name of the employee. Must not be blank.
 * @property dob The date of birth of the employee.
 * @property maritalStatus The marital status of the employee.
 * @property honorific The honorific or title of the employee.
 * @property contact Optional contact details of the employee.
 */
@Serializable
data class EmployeeParams(
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val maritalStatus: MaritalStatus,
    val honorific: Honorific,
    val contact: ContactParams? = null
) {
    init {
        require(firstName.isNotBlank()) { "First name can't be empty." }
        require(lastName.isNotBlank()) { "Last name can't be empty." }
    }
}
