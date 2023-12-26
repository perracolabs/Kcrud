/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.frameworks.expedia

/**
 * Annotation for controlled access to the Expedia GraphQL API code,
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the Expedia GraphQL context.")
@Retention(AnnotationRetention.BINARY)
annotation class ExpediaAPI
