/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.context

/**
 * This class illustrative an example of how to represent a user context within the application,
 * and should be adapted according to specific application requirements.
 *
 * The `userId` parameter could represent a unique identifier (such as an ID or username)
 * from which more detailed user information could be resolved.
 *
 * @property userId The unique identifier of the user, which can be null or blank for anonymous users.
 */
internal class ContextUser(private val userId: String?) {

    @Suppress("MemberVisibilityCanBePrivate")
    val isAnonymous: Boolean by lazy {
        userId.isNullOrBlank()
    }

    val username: String by lazy {
        if (isAnonymous) ANONYMOUS else userId!!
    }

    private companion object {
        const val ANONYMOUS = "anonymous"
    }
}

