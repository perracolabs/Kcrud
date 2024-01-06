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
import kcrud.base.data.pagination.Pageable
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

    @Test
    fun testPagination() = testApplication {
        println("Running testPagination...")

        // Force the application module to load.
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        transaction {
            val contactRepository = ContactRepository()
            val employeeRepository = EmployeeRepository(contactRepository)
            val employeeService = EmployeeService(employeeRepository)

            val totalRecords = 10

            // No records.
            employeeService.findAll(pageable = Pageable(page = 1, size = totalRecords)).also { page ->
                assertEquals(1, page.info.totalPages)
                assertEquals(0, page.info.totalElements)
                assertEquals(totalRecords, page.info.elementsPerPage)
                assertEquals(0, page.info.elementsInPage)
                assertEquals(1, page.info.pageIndex)
                assertEquals(false, page.info.hasNext)
                assertEquals(false, page.info.hasPrevious)
                assertEquals(true, page.info.isFirst)
                assertEquals(true, page.info.isLast)
            }

            // Create test records.
            (1..totalRecords).forEach { index ->
                val employeeRequest = EmployeeRequest(
                    firstName = "Pepito $index",
                    lastName = "Perez $index",
                    dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1 + index),
                    honorific = Honorific.MR,
                    maritalStatus = MaritalStatus.SINGLE
                )

                employeeService.create(employeeRequest)
            }

            // Test 2 pages.
            employeeService.findAll(pageable = Pageable(page = 1, size = totalRecords / 2)).also { page ->
                assertEquals(2, page.info.totalPages)
                assertEquals(totalRecords, page.info.totalElements)
                assertEquals(totalRecords / 2, page.info.elementsPerPage)
                assertEquals(totalRecords / 2, page.info.elementsInPage)
                assertEquals(1, page.info.pageIndex)
                assertEquals(true, page.info.hasNext)
                assertEquals(false, page.info.hasPrevious)
                assertEquals(true, page.info.isFirst)
                assertEquals(false, page.info.isLast)
            }

            // 1 Page
            employeeService.findAll(pageable = Pageable(page = 1, size = 0)).also { page ->
                assertEquals(1, page.info.totalPages)
                assertEquals(totalRecords, page.info.totalElements)
                assertEquals(totalRecords, page.info.elementsPerPage)
                assertEquals(totalRecords, page.info.elementsInPage)
                assertEquals(1, page.info.pageIndex)
                assertEquals(false, page.info.hasNext)
                assertEquals(false, page.info.hasPrevious)
                assertEquals(true, page.info.isFirst)
                assertEquals(true, page.info.isLast)
            }

            rollback()
        }

        println("testPagination completed successfully.")
    }
}
