/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employee

import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.models.contact.Contact
import com.kcrud.utils.AgeDelegate
import com.kcrud.utils.SUUID
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the model for an employee.
 *
 * @property id The employee's id.
 * @property firstName The employee's first name.
 * @property lastName The employee's last name.
 * @property dob The employee's date of birth.
 * @property maritalStatus The employee's marital status.
 * @property contact The employee's contact details.
 */
@Serializable
data class Employee(
    val id: SUUID,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val maritalStatus: MaritalStatus,
    val honorific: Honorific,
    val contact: Contact? = null
) {
    // To serialize default values 'encodeDefaults' in the json configuration must be set to True.
    val fullName: String = "$firstName $lastName"

    // This is an example of how to circumvent the limitation that
    // kotlinx.serialization does not support delegated properties.
    // The regular, serializable property 'age' is manually assigned
    // the value of the delegated property 'ageDelegate'. This makes
    // 'age' serializable while still using the logic contained in AgeDelegate.
    private val ageDelegate: Int by AgeDelegate(dob)
    val age = ageDelegate

    companion object {
        fun fromTableRow(row: ResultRow): Employee {
            return Employee(
                id = row[EmployeeTable.id],
                firstName = row[EmployeeTable.firstName],
                lastName = row[EmployeeTable.lastName],
                dob = row[EmployeeTable.dob],
                maritalStatus = row[EmployeeTable.maritalStatus],
                honorific = row[EmployeeTable.honorific],
                contact = row[EmployeeTable.contactId]?.let { Contact.fromTableRow(row) }
            )
        }
    }
}
