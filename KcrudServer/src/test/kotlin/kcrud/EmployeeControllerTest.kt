/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud

import io.mockk.coEvery
import io.mockk.mockk
import kcrud.server.domain.entities.contact.Contact
import kcrud.server.domain.entities.employee.Employee
import kcrud.server.domain.entities.employee.types.Honorific
import kcrud.server.domain.entities.employee.types.MaritalStatus
import kcrud.server.domain.repositories.employee.IEmployeeRepository
import kcrud.server.domain.services.EmployeeService
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

        coEvery { mockRepository.findById(employeeId) } returns mockEmployee

        val result = employeeService.findById(employeeId)
        assertEquals(mockEmployee, result)

        println("testGetEmployee completed successfully.")
    }
}