/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.context

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

