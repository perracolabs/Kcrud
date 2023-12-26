/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.expedia.types

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLType
import kotlinx.datetime.LocalDate
import java.util.*
import kotlin.reflect.KType

/**
 * Generate custom GraphQL types which are not supported by default.
 */
class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (type.classifier) {
            UUID::class -> GraphUUIDType
            LocalDate::class -> GraphLocalDateType
            else -> super.willGenerateGraphQLType(type)
        }
    }
}
