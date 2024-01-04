/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.admin.env.security.user

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * [ApplicationCall] extension function to retrieve the user from the current call's [principal].
 *
 * Attempts first to retrieve the user from a [JWTPrincipal], which is typically used
 * with JWT-based authentication. If a JWTPrincipal is not present, then falls back to
 * checking for a [ContextPrincipal], which is commonly used with other authentication methods.
 *
 * @return A [ContextUser] instance, or null if the user could not be retrieved.
 */
internal fun ApplicationCall.userFromCall(): ContextUser? {
    // First try to resolve the user from a JWTPrincipal.
    principal<JWTPrincipal>()?.let { jwtPrincipal ->
        ContextUser.fromJwtPayload(payload = jwtPrincipal.payload)?.let { contextUser ->
            return contextUser
        }
    }

    // If JWTPrincipal is not present, then try from a ContextPrincipal.
    return principal<ContextPrincipal>()?.let { principal ->
        ContextUser.fromContextPrincipal(principal = principal)
    }
}
