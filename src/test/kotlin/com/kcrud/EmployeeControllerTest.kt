package com.kcrud

import com.kcrud.controllers.EmployeeController
import com.kcrud.data.entities.ContactEntity
import com.kcrud.data.entities.EmployeeEntity
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.services.EmployeeService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.koin.test.KoinTest
import kotlin.test.Test


class EmployeeControllerTest : KoinTest {

    // Mock dependencies.
    private val mockRepository = mockk<IEmployeeRepository>()
    private val employeeService = EmployeeService(mockRepository)
    private val employeeController = EmployeeController(employeeService)

    @Test
    fun testGetEmployee() = runBlocking {
        println("Running testGetEmployee...")

        // Given
        val employeeId = 1
        val mockEmployee = EmployeeEntity(
            id = employeeId,
            firstName = "Pepito",
            lastName = "Paquito",
            dob = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 1),
            contact = ContactEntity(
                id = 1,
                email = "saco.paco@email.com",
                phone = "123-456-789"
            )
        )

        assert(mockEmployee.age != 0)
        assert(mockEmployee.fullName.isNotBlank())

        coEvery { mockRepository.findById(employeeId) } returns mockEmployee

        // Simulate the call.
        val call = mockk<ApplicationCall>(relaxed = true)
        coEvery { call.parameters["id"] } returns employeeId.toString()

        // Capture the response.
        val slot = slot<Any>()
        coEvery { call.respond(capture(slot)) } answers {}

        // Invoke the method.
        employeeController.get(call)

        // Print the response
        println("Response: ${slot.captured}")

        // Verify interactions.
        coVerify {
            mockRepository.findById(employeeId)
            call.respond(mockEmployee)
        }

        println("testGetEmployee completed successfully.")
    }
}
