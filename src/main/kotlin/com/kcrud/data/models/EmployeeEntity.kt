package com.kcrud.data.models

import com.kcrud.utils.AgeDelegate
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class EmployeeEntity(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val fullName: String = "$firstName $lastName"
) {
    // This is an example of how to circumvent the limitation that
    // kotlinx.serialization does not support delegated properties.
    // The regular, serializable property 'age' is manually assigned
    // the value of the delegated property 'ageDelegate'. This makes
    // 'age' serializable while still using the logic contained in AgeDelegate.
    private val ageDelegate: Int by AgeDelegate(dob)
    val age = ageDelegate
}

/**
 * Employee entity details for deserialization.
 */
@Serializable
data class EmployeeEntityIn(
    val firstName: String,
    val lastName: String,
    val dob: LocalDate
) {
    init {
        require(firstName.isNotBlank()) { "First name can't be empty." }
        require(lastName.isNotBlank()) { "Last name can't be empty." }
    }
}

/**
 * Employee entity for patching individual fields.
 */
@Serializable
data class EmployeePatchDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val dob: LocalDate? = null
) {
    init {
        firstName?.let {
            require(it.isNotBlank()) { "First name can't be empty." }
        }
        lastName?.let {
            require(it.isNotBlank()) { "Last name can't be empty." }
        }
    }
}
