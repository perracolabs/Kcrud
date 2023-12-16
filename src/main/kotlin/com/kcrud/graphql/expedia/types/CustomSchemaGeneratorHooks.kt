/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.types

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLType
import kotlinx.datetime.LocalDate
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        UUID::class -> graphqlUUIDType
        LocalDate::class -> graphqlLocalDateType
        else -> null
    }
}
