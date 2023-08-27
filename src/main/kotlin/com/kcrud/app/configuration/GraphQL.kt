package com.kcrud.app.configuration

import com.apurebase.kgraphql.GraphQL
import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeInput
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.data.repositories.EmployeeRepository
import io.ktor.server.application.*
import kotlinx.datetime.LocalDate
import java.time.DayOfWeek
import java.time.Month


fun Application.configureGraphQL() {
    install(GraphQL) {
        playground = true

        val repository = EmployeeRepository()

        schema {

            enum<DayOfWeek> {
                description = "Day of week"
            }

            enum<Month> {
                description = "Month in a year"
            }

            // Queries

            type<EmployeeEntity> {
                description = "Query type definition for employee."
            }

            query("employee") {
                description = "Returns a single employee given its id."
                resolver { id: Int -> repository.findById(id = id) }
            }

            query("employees") {
                description = "Returns all existing employees."
                resolver { -> repository.findAll() }
            }

            // Mutations

            inputType<EmployeeInput> {
                name = "Input type definition for Employee."
            }

            inputType<EmployeePatchDTO> {
                description = "Input type definition for updating employees."
            }

            stringScalar<LocalDate> {
                serialize = { date -> date.toString() }
                deserialize = { str -> LocalDate.parse(str) }
            }

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
