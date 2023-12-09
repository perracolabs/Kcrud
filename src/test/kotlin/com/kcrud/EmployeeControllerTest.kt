/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud

import com.kcrud.data.models.contact.Contact
import com.kcrud.data.models.employee.Employee
import com.kcrud.data.repositories.employee.IEmployeeRepository
import com.kcrud.services.EmployeeService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class EmployeeServiceTest {

    private val mockRepository = mockk<IEmployeeRepository>()
    private val employeeService = EmployeeService(mockRepository)

    @Test
    fun testGetEmployee() = runBlocking {
        println("Running testGetEmployee...")

        // Given
        val employeeId = UUID.randomUUID()
        val mockEmployee = Employee(
            id = employeeId,
            firstName = "Pepito",
            lastName = "Paquito",
            dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1),
            contact = Contact(
                id = UUID.randomUUID(),
                email = "saco.paco@email.com",
                phone = "123-456-789"
            )
        )

        assert(mockEmployee.age != 0)
        assert(mockEmployee.fullName.isNotBlank())

        coEvery { mockRepository.findById(employeeId) } returns mockEmployee

        val result = employeeService.findById(employeeId)
        assertEquals(mockEmployee, result)

        println("testGetEmployee completed successfully.")
    }
}