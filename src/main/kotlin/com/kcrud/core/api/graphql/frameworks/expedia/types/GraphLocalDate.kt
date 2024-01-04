/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.api.graphql.frameworks.expedia.types

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.*
import kotlinx.datetime.LocalDate
import java.util.*

/**
 * Generate custom GraphQL for the Kotlinx LocalDate type.
 */
val GraphLocalDateType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("LocalDate")
    .description("Type representing a LocalDate.")
    .coercing(LocalDateCoercing)
    .build()

object LocalDateCoercing : Coercing<LocalDate, String> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String = runCatching {
        if (dataFetcherResult is LocalDate) {
            dataFetcherResult.toString()
        } else {
            throw CoercingSerializeException("Data fetcher result $dataFetcherResult is not a valid LocalDate.")
        }
    }.getOrElse {
        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String.")
    }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): LocalDate = runCatching {
        LocalDate.parse(serialize(input, graphQLContext, locale))
    }.getOrElse {
        throw CoercingParseValueException("Expected valid LocalDate but was $input.")
    }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): LocalDate {
        val dateString = (input as? StringValue)?.value
        return runCatching {
            LocalDate.parse(dateString!!)
        }.getOrElse {
            throw CoercingParseLiteralException("Expected valid LocalDate literal but was $dateString.")
        }
    }
}