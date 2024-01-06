/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kcrud.server.domain.entities.contact.Contact
import kcrud.server.domain.entities.employee.Employee
import kcrud.server.domain.entities.employee.EmployeeRequest
import kcrud.server.domain.entities.employee.types.Honorific
import kcrud.server.domain.entities.employee.types.MaritalStatus
import kcrud.server.domain.repositories.contact.ContactRepository
import kcrud.server.domain.repositories.employee.EmployeeRepository
import kcrud.server.domain.repositories.employee.IEmployeeRepository
import kcrud.server.domain.services.EmployeeService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EmployeeServiceTest {

    @Test
    fun testGetEmployee() = runBlocking {
        println("Running testGetEmployee...")

        val employeeId = UUID.randomUUID()
        val mockEmployee = Employee(
            id = employeeId,
            firstName = "Pepito",
            lastName = "Paquito",
            dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1),
            honorific = Honorific.MR,
            maritalStatus = MaritalStatus.SINGLE,
            contact = Contact(
                id = UUID.randomUUID(),
                email = "saco.paco@email.com",
                phone = "123-456-789"
            )
        )

        assert(mockEmployee.age != 0)
        assert(mockEmployee.fullName.isNotBlank())

        val mockRepository = mockk<IEmployeeRepository>()
        val employeeService = EmployeeService(mockRepository)
        coEvery { mockRepository.findById(employeeId) } returns mockEmployee

        val result = employeeService.findById(employeeId)
        assertEquals(mockEmployee, result)

        println("testGetEmployee completed successfully.")
    }

    @Test
    fun testCreateUpdateEmployee() = testApplication {
        println("Running testCreateUpdateEmployee...")

        // Force the application module to load.
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        val employeeRequest = EmployeeRequest(
            firstName = "Pepito",
            lastName = "Perez",
            dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1),
            honorific = Honorific.MR,
            maritalStatus = MaritalStatus.MARRIED
        )

        transaction {
            val contactRepository = ContactRepository()
            val employeeRepository = EmployeeRepository(contactRepository)
            val employeeService = EmployeeService(employeeRepository)

            // Create
            val employee: Employee = employeeService.create(employeeRequest)
            assertEquals(employeeRequest.firstName, employee.firstName)

            // Update
            MaritalStatus.entries.forEachIndexed { index, maritalStatus ->
                val updateEmployeeRequest = EmployeeRequest(
                    firstName = "Pepito $index",
                    lastName = "Perez $index",
                    dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1 + index),
                    honorific = Honorific.MR,
                    maritalStatus = maritalStatus
                )

                val updatedEmployee: Employee? = employeeService.update(employee.id, updateEmployeeRequest)
                assertNotNull(updatedEmployee)
                assertEquals(updateEmployeeRequest.firstName, updatedEmployee.firstName)
                assertEquals(updateEmployeeRequest.lastName, updatedEmployee.lastName)
                assertEquals(updateEmployeeRequest.dob, updatedEmployee.dob)
                assertEquals(updateEmployeeRequest.maritalStatus, updatedEmployee.maritalStatus)
            }

            rollback()
        }

        println("testCreateUpdateEmployee completed successfully.")
    }
}
