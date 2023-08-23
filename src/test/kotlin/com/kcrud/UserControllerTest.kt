package com.kcrud

import com.kcrud.controllers.UserController
import com.kcrud.data.models.UserEntity
import com.kcrud.data.repositories.IUserRepository
import com.kcrud.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.koin.test.KoinTest
import kotlin.test.*


class UserControllerTest : KoinTest {

    // Mock dependencies.
    private val mockRepository = mockk<IUserRepository>()
    private val userService = UserService(mockRepository)
    private val userController = UserController(userService)

    @Test
    fun testGetUser() = runBlocking {
        println("Running testGetUser...")

        // Given
        val userId = 1
        val mockUser = UserEntity(id = userId, name = "Test User Name", age = 30)
        coEvery { mockRepository.findById(userId) } returns mockUser

        // Simulate the call.
        val call = mockk<ApplicationCall>(relaxed = true)
        coEvery { call.parameters["id"] } returns userId.toString()

        // Capture the response.
        val slot = slot<Any>()
        coEvery { call.respond(capture(slot)) } answers {}

        // Invoke the method.
        userController.get(call)

        // Print the response
        println("Response: ${slot.captured}")

        // Verify interactions.
        coVerify {
            mockRepository.findById(userId)
            call.respond(mockUser)
        }

        println("testGetUser completed successfully.")
    }
}
