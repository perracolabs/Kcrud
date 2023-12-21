/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.employee.types.Honorific
import com.kcrud.data.models.employee.types.MaritalStatus
import com.kcrud.data.models.employment.types.WorkModality
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.utils.toUUID
import kotlinx.datetime.LocalDate
import java.time.DayOfWeek
import java.time.Month
import java.util.*

/**
 * Demonstrates modularization of GraphQL schemas for scalability.
 * This object serves as an example of how to modularize different components of a GraphQL schema.
 *
 * By following this pattern, it becomes easier to split a large and growing schema into separate
 * files for better maintainability.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class SharedTypes(private val schemaBuilder: SchemaBuilder) {

    /**
     * Configures common types like enums and scalars that are used in both queries and mutations.
     */
    fun configure(): SharedTypes {
        schemaBuilder.apply {
            enum<DayOfWeek> {
                description = "Day of week."
            }
            enum<Month> {
                description = "Month in a year."
            }
            enum<MaritalStatus> {
                description = "The employee's marital status."
            }
            enum<Honorific> {
                description = "The employee's honorific title."
            }
            enum<WorkModality> {
                description = "The employment's work modality."
            }
            stringScalar<LocalDate> {
                serialize = { date -> date.toString() }
                deserialize = { str -> LocalDate.parse(str) }
            }
            stringScalar<UUID> {
                description = "Unique identifier"
                serialize = { uuid -> uuid.toString() }
                deserialize = { str -> str.toUUID() }
            }
        }

        return this
    }
}
