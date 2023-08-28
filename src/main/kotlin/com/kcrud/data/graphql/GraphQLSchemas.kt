package com.kcrud.data.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeInput
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.data.repositories.EmployeeRepository
import kotlinx.datetime.LocalDate
import java.time.DayOfWeek
import java.time.Month


/**
 * Demonstrates modularization of GraphQL schemas for scalability.
 * This object serves as an example of how to modularize different components of a GraphQL schema.
 * By following this pattern, it becomes easier to split a large and growing schema into separate
 * files for better maintainability.
 */
object GraphQLSchemas {

    /**
     * Configures common types like enums and scalars that are used in both queries and mutations.
     * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
     */
    fun configureTypes(schemaBuilder: SchemaBuilder) {
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
    }

    /**
     * Configures query types specifically.
     * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
     */
    fun configureQueryTypes(schemaBuilder: SchemaBuilder) {
        schemaBuilder.apply {
            type<EmployeeEntity> {
                description = "Query type definition for employee."
            }
        }
    }

    /**
     * Configures query resolvers to fetch data.
     * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
     * @param repository The data repository used in query resolvers.
     */
    fun configureQueries(schemaBuilder: SchemaBuilder, repository: EmployeeRepository) {
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
    }

    /**
     * Configures input types for mutations.
     * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
     */
    fun configureMutationInputs(schemaBuilder: SchemaBuilder) {
        schemaBuilder.apply {
            inputType<EmployeeInput> {
                name = "Input type definition for Employee."
            }

            inputType<EmployeePatchDTO> {
                description = "Input type definition for updating employees."
            }
        }
    }

    /**
     * Configures mutation resolvers to modify data.
     * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
     * @param repository The data repository used in mutation resolvers.
     */
    fun configureMutations(schemaBuilder: SchemaBuilder, repository: EmployeeRepository) {
        schemaBuilder.apply {
            mutation("createEmployee") {
                description = "Creates a new employee."
                resolver { employee: EmployeeInput -> repository.create(employee = employee) }
            }

            mutation("updateEmployee") {
                description = "Updates an existing employee."
                resolver { id: Int, employee: EmployeePatchDTO -> repository.patch(id = id, employeePatch = employee) }
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
    }
}
