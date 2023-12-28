/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.security.user

import com.auth0.jwt.interfaces.Payload
import io.ktor.server.auth.*

/**
 * Example of how to represent a user context within the application,
 *
 * @property userId The unique user identifier, which can be null or blank for anonymous users.
 * @property role The role of the user.
 */
internal data class ContextUser(val userId: String?, val role: UserRole) {

    companion object {

        /**
         * Creates a [ContextUser] instance from a JWT [Payload].
         *
         * @param payload The JWT [Payload] containing user-related claims.
         * @return A [ContextUser] instance if both userId and userRole are present and valid, null otherwise.
         */
        fun fromJwtPayload(payload: Payload): ContextUser? {
            val userId: String? = payload.getClaim(KEY_USER_ID)?.asString()
            val userRole: String? = payload.getClaim(KEY_USER_ROLE)?.asString()

            return takeIf { !userId.isNullOrBlank() && !userRole.isNullOrBlank() }?.let {
                ContextUser(userId = userId, role = UserRole.valueOf(userRole!!))
            }
        }

        /**
         * Creates a [ContextUser] instance from a [UserIdPrincipal].
         * Assumes that all users represented by UserIdPrincipal are of the role ADMIN.
         *
         * Note: In a real application, the role should ideally be retrieved from a database or another source.
         *
         * @param userIdPrincipal The [UserIdPrincipal] representing the user.
         * @return A [ContextUser] instance if the userId is present and non-blank, null otherwise.
         */
        fun fromUserIdPrincipal(userIdPrincipal: UserIdPrincipal?): ContextUser? {
            return userIdPrincipal?.name?.takeIf { it.isNotBlank() }?.let { userId ->
                // In this case we are assuming that all users are admins.
                // Instead, should be retrieved from the database.
                ContextUser(userId = userId, role = UserRole.ADMIN)
            }
        }

        const val KEY_USER = "user"
        const val KEY_USER_ID = "userId"
        const val KEY_USER_ROLE = "userRole"
    }
}
