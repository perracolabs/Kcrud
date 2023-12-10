/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employee

import com.kcrud.data.models.contact.ContactInput
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employee.
 *
 * @property firstName The employee's first name.
 * @property lastName The employee's last name.
 * @property dob The employee's date of birth.
 * @property maritalStatus The employee's marital status.
 * @property contact The employee's contact details.
 */
@Serializable
data class EmployeeInput(
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val maritalStatus: MaritalStatus,
    val honorific: Honorific,
    val contact: ContactInput? = null
) {
    init {
        require(firstName.isNotBlank()) { "First name can't be empty." }
        require(lastName.isNotBlank()) { "Last name can't be empty." }
    }
}
