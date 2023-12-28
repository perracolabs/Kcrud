/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.security.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


/**
 * [ApplicationCall] extension function to retrieve the user ID from the current call's [principal].
 *
 * Attempts first to retrieve the user ID from a JWTPrincipal, which is typically used
 * with JWT-based authentication. If a JWTPrincipal is not present, falls back to checking
 * for a UserIdPrincipal, which is commonly used with other authentication methods.
 *
 * @return The user ID as a String if available, or null if neither a JWTPrincipal nor a UserIdPrincipal is present.
 */
fun ApplicationCall.userIdFromPrincipal(): String? {
    // Check for JWTPrincipal and extract the user ID if available.
    val jwtPrincipal = principal<JWTPrincipal>()
    val userIdFromJwt = jwtPrincipal?.payload?.getClaim(AuthenticationToken.KEY_USER_ID)?.asString()
    if (!userIdFromJwt.isNullOrBlank()) {
        return userIdFromJwt
    }

    // If JWTPrincipal is not present, check for UserIdPrincipal.
    val userIdPrincipal = principal<UserIdPrincipal>()
    return userIdPrincipal?.name
}
