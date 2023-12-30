/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.frameworks.kgraphql

/**
 * Annotation for controlled access to the KGraphQL API.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the KGraphQL API.")
@Retention(AnnotationRetention.BINARY)
annotation class KGraphQLAPI
