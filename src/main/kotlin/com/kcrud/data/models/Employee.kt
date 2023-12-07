/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models

import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.utils.AgeDelegate
import com.kcrud.utils.SUUID
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Employee(
    val id: SUUID? = null,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val contact: Contact
) {
    init {
        require(firstName.isNotBlank()) { "First name can't be empty." }
        require(lastName.isNotBlank()) { "Last name can't be empty." }
    }

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
                contact = Contact.fromTableRow(row)
            )
        }
    }
}
