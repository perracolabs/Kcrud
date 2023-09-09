/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.entities

import com.kcrud.utils.AgeDelegate
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeEntity(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val contact: ContactEntity
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
}
