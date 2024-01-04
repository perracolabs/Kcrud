/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.admin.env.security.user

import com.auth0.jwt.interfaces.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Example of how to represent a user context within the application,
 *
 * @property id The unique user identifier, which can be null or blank for anonymous users.
 * @property role The role of the user.
 */
@Serializable
internal data class ContextUser(val id: String, val role: UserRole) {

    companion object {

        /**
         * Creates a [ContextUser] instance from a JWT [Payload].
         *
         * @param payload The JWT [Payload] containing user-related claims.
         * @return A [ContextUser] instance if both userId and userRole are present and valid, null otherwise.
         */
        fun fromJwtPayload(payload: Payload): ContextUser? {
            val userPayload: String? = payload.getClaim(KEY_USER)?.asString()

            return takeIf { !userPayload.isNullOrBlank() }?.let {
                val user: ContextUser = Json.decodeFromString(
                    deserializer = serializer(),
                    string = userPayload!!
                )
                ContextUser(id = user.id, role = user.role)
            }
        }

        /**
         * Creates a [ContextUser] instance from a [ContextPrincipal].
         *
         * @param principal The [ContextPrincipal] representing the user.
         * @return A [ContextUser] instance if the userId is present and non-blank, null otherwise.
         */
        fun fromContextPrincipal(principal: ContextPrincipal?): ContextUser? {
            return principal?.let { contextPrincipal ->
                // In this case we are assuming that all users are admins.
                // Instead, should be retrieved from the database.
                ContextUser(id = contextPrincipal.user.id, role = contextPrincipal.user.role)
            }
        }

        const val KEY_USER = "user"
    }
}
