package com.kcrud.data.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.entities.EmployeeEntity
import com.kcrud.data.repositories.IEmployeeRepository
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
 * @param repository The data repository used in mutation resolvers.
 */
class GraphQLSchemas(private val schemaBuilder: SchemaBuilder, private val repository: IEmployeeRepository) {

    /**
     * Configures common types like enums and scalars that are used in both queries and mutations.
     */
    fun configureCommonTypes(): GraphQLSchemas {
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

    /**
     * Configures query types specifically.
     */
    fun configureQueryTypes(): GraphQLSchemas {
        schemaBuilder.apply {
            type<EmployeeEntity> {
                description = "Query type definition for employee."
            }
        }

        return this
    }

    /**
     * Configures query resolvers to fetch data.
     */
    fun configureQueries(): GraphQLSchemas {
        schemaBuilder.apply {
            query("employee") {
                description = "Returns a single employee given its id."
                resolver { id: Int -> repository.findById(id = id) }
            }
            query("employees") {
                description = "Returns all existing employees."
                resolver { -> repository.findAll() }
            }
        }

        return this
    }

    /**
     * Configures input types for mutations.
     */
    fun configureMutationInputs(): GraphQLSchemas {
        schemaBuilder.apply {
            inputType<EmployeeEntity> {
                name = "Input type definition for Employee."
            }
        }

        return this
    }

    /**
     * Configures mutation resolvers to modify data.
     */
    fun configureMutations(): GraphQLSchemas {
        schemaBuilder.apply {
            mutation("createEmployee") {
                description = "Creates a new employee."
                resolver { employee: EmployeeEntity -> repository.create(employee = employee) }
            }

            mutation("updateEmployee") {
                description = "Updates an existing employee."
                resolver { id: Int, employee: EmployeeEntity -> repository.update(id = id, employee = employee) }
            }

            mutation("deleteEmployee") {
                description = "Deletes an existing employee."
                resolver { id: Int -> repository.delete(id = id) }
            }

            mutation("deleteAllEmployees") {
                description = "Deletes all existing employees."
                resolver { -> repository.deleteAll() }
            }
        }

        return this
    }
}
