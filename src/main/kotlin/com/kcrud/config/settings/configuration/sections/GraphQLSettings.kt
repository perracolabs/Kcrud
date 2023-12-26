/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.configuration.sections

import com.kcrud.api.graphql.utils.GraphQLFramework

/**
 * GraphQL related settings.
 *
 * @property isEnabled Whether GraphQL is enabled.
 * @property framework The GraphQL framework to use.
 * @property playground Whether to enable the GraphQL Playground.
 * @property dumpSchema Whether to dump the GraphQL schema.
 */
internal data class GraphQLSettings(
    val isEnabled: Boolean,
    val framework: GraphQLFramework,
    val playground: Boolean,
    val dumpSchema: Boolean
)
