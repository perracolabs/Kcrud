/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.security.user

/**
 * Example of user roles. Ideally these should be defined
 * at database level instead of being hardcoded.
 */
enum class UserRole {
    ADMIN,
    GUEST,
    USER
}
