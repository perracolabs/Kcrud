/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.database.annotation

/**
 * Annotation for controlled access to the Database API.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the Database API.")
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseAPI
