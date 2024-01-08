/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employee

import kcrud.base.data.serializers.SUUID
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

    /**
     * Workaround example for kotlinx-serialization's inability to serialize delegated properties.
     * A private delegated property `_age` using `AgeDelegate` encapsulates age calculation logic.
     * The public property `age` is assigned `_age`'s value, ensuring serialization compatibility.
     * This pattern maintains property delegation benefits, like encapsulation and potential lazy
     * evaluation, while adapting to kotlinx-serialization's limitations.
     */
    private val _age: Int by AgeDelegate(dob)
    val age: Int = _age

    companion object {
        fun from(row: ResultRow): Employee {
            val contact: Contact? = row.getOrNull(ContactTable.id)?.let { Contact.from(row = row) }
            return Employee(
                id = row[EmployeeTable.id],
                firstName = row[EmployeeTable.firstName],
                lastName = row[EmployeeTable.lastName],
                dob = row[EmployeeTable.dob],
                maritalStatus = row[EmployeeTable.maritalStatus],
                honorific = row[EmployeeTable.honorific],
                contact = contact
            )
        }
    }
}
