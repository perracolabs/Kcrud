/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.entities.contact

import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employee's contact details.
 *
 * @property email The contact's email. Must not be blank.
 * @property phone The contact's phone. Must not be blank.
 */
@Serializable
data class ContactRequest(
    val email: String,
    val phone: String
) {
    init {
        require(email.isNotBlank()) { "Email can't be empty." }
        require(phone.isNotBlank()) { "Phone can't be empty." }
    }
}
