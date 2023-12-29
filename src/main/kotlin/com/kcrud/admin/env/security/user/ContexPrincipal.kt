/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.security.user

import io.ktor.server.auth.*

/**
 * Example of how to represent a user principal context,
 * which can be passed in the application authentication pipeline.
 *
 * @property userId The unique user identifier.
 */
internal data class ContextPrincipal(private val userId: String) : Principal {
    /**
     * The role of the user.
     * This would typically be retrieved from a database or another source.
     */
    val user: ContextUser = ContextUser(id = userId, role = UserRole.ADMIN)
}

