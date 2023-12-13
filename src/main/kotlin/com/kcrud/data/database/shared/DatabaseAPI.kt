/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.shared

/**
 * Annotation designed for controlled access in database management code,
 * offering a workaround for the lack of package-private visibility in Kotlin.
 * It enables selective access for classes, even across different packages,
 * but restricts this to database management contexts.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used in database-management contexts.")
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseAPI
