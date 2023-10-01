/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import kotlinx.datetime.LocalDate
import java.time.DayOfWeek
import java.time.Month

/**
 * Demonstrates modularization of GraphQL schemas for scalability.
 * This object serves as an example of how to modularize different components of a GraphQL schema.
 *
 * By following this pattern, it becomes easier to split a large and growing schema into separate
 * files for better maintainability.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
class GraphQLCommonSchema(private val schemaBuilder: SchemaBuilder) {

    /**
     * Configures common types like enums and scalars that are used in both queries and mutations.
     */
    fun configureCommonTypes(): GraphQLCommonSchema {
        schemaBuilder.apply {
            enum<DayOfWeek> {
                description = "Day of week."
            }
            enum<Month> {
                description = "Month in a year."
            }
            stringScalar<LocalDate> {
                serialize = { date -> date.toString() }
                deserialize = { str -> LocalDate.parse(str) }
            }
        }

        return this
    }
}
