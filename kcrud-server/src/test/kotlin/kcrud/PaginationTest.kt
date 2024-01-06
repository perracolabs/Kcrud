/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kcrud.base.data.pagination.Pageable
import kcrud.server.domain.entities.employee.EmployeeRequest
import kcrud.server.domain.entities.employee.types.Honorific
import kcrud.server.domain.entities.employee.types.MaritalStatus
import kcrud.server.domain.repositories.contact.ContactRepository
import kcrud.server.domain.repositories.employee.EmployeeRepository
import kcrud.server.domain.services.EmployeeService
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationTest {

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
