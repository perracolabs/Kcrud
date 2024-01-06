/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employee

import kcrud.base.data.utils.SUUID
import kcrud.base.utils.AgeDelegate
import kcrud.server.data.tables.ContactTable
import kcrud.server.data.tables.EmployeeTable
import kcrud.server.domain.entities.contact.Contact
import kcrud.server.domain.entities.employee.types.Honorific
import kcrud.server.domain.entities.employee.types.MaritalStatus
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the entity for an employee.
 *
 * @property id The employee's id.
 * @property firstName The first name of the employee.
 * @property lastName The last name of the employee.
 * @property dob The date of birth of the employee.
 * @property maritalStatus The marital status of the employee.
 * @property honorific The honorific or title of the employee.
 * @property contact Optional contact details of the employee.
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

    // Although 'age' could be simply assigned by calling 'DateTimeUtils.calculateAge(dob)',
    // this example demonstrates a workaround for kotlinx.serialization's limitation with
    // delegated properties, as such cannot serialize delegated properties.
    // The regular serializable property 'age' is manually assigned the value from the
    // delegated property 'ageDelegate'. This approach ensures 'age' remains serializable
    // while incorporating the logic within AgeDelegate.
    private val ageDelegate: Int by AgeDelegate(dob)
    val age = ageDelegate

    companion object {
        fun toEntity(row: ResultRow): Employee {
            return Employee(
                id = row[EmployeeTable.id],
                firstName = row[EmployeeTable.firstName],
                lastName = row[EmployeeTable.lastName],
                dob = row[EmployeeTable.dob],
                maritalStatus = row[EmployeeTable.maritalStatus],
                honorific = row[EmployeeTable.honorific],
                contact = row.getOrNull(ContactTable.id)?.let { Contact.toEntity(row = row) }
            )
        }
    }
}
