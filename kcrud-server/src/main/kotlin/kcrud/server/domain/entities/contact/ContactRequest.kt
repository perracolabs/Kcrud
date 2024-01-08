/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.contact

import kcrud.base.data.serializers.EmailString
import kcrud.server.domain.exceptions.EmployeeError
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employee's contact details.
 *
 * This entity serves as example of how to use the [EmailString],
 * which is a typealias for a serializable String that must be a valid email.
 * Note that the project also includes examples about how perform verifications
 * at service level, instead of using serializers.
 * Such validators can send to the client a more detailed error than would
 * do a serializer. See: [EmployeeError].
 *
 * @property email The contact's email. Must not be blank.
 * @property phone The contact's phone. Must not be blank.
 */
@Serializable
data class ContactRequest(
    val email: EmailString,
    val phone: String
)
