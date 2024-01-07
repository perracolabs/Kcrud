/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.contact

import kcrud.base.data.serializers.EmailString
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employee's contact details.
 *
 * @property email The contact's email. Must not be blank.
 * @property phone The contact's phone. Must not be blank.
 */
@Serializable
data class ContactRequest(
    val email: EmailString,
    val phone: String
)
