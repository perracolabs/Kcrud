/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.types

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.*
import java.util.*


val graphqlUUIDType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("Type representing a system UUID.")
    .coercing(UUIDCoercing)
    .build()

object UUIDCoercing : Coercing<UUID, String> {
    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID = runCatching {
        UUID.fromString(serialize(input, graphQLContext, locale))
    }.getOrElse {
        throw CoercingParseValueException("Expected valid UUID but was $input.")
    }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): UUID {
        val uuidString = (input as? StringValue)?.value
        return runCatching {
            UUID.fromString(uuidString)
        }.getOrElse {
            throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString.")
        }
    }

    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String = runCatching {
        dataFetcherResult.toString()
    }.getOrElse {
        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String.")
    }
}