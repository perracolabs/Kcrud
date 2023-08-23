package com.kcrud.controllers

import com.kcrud.data.models.UserEntity
import com.kcrud.data.models.UserPatchDTO
import com.kcrud.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent


class UserController(private val service: UserService) : KoinComponent {

    /**
     * Handles GET request to retrieve details of a specific user.
     * Responds with the user details if found, otherwise appropriate error messages.
     */
    suspend fun get(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toIntOrNull()

        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID.")
            return
        }

        val user = service.findById(userId)

        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User ID not found: $userId")
        } else {
            call.respond(user)
        }
    }

    /**
     * Handles GET request to retrieve details of all users.
     * Responds with a list of all users.
     */
    suspend fun getAll(call: ApplicationCall) {
        val users = service.findAll()
        call.respond(users)
    }

    /**
     * Handles POST request to create a new user.
     * Reads the user details from the request, creates a new user, and responds with the created user details.
     */
    suspend fun create(call: ApplicationCall) {
        val user = call.receive<UserEntity>()
        val newUser = service.create(user)
        call.respond(HttpStatusCode.Created, newUser)
    }

    /**
     * Handles PUT request to update details of a specific user.
     * Reads the updated user details from the request, updates the user, and responds with the updated user details.
     */
    suspend fun update(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toIntOrNull()

        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID.")
            return
        }

        val userData = call.receive<UserEntity>()
        val updatedUser = service.update(userId, userData)

        if (updatedUser == null) {
            call.respond(HttpStatusCode.NotFound, "User ID not found: $userId")
        } else {
            call.respond(HttpStatusCode.OK, updatedUser)
        }
    }

    /**
     * Handles PATCH request to update details of a specific user.
     * Reads the updated user details from the request, updates the user, and responds with the updated user details.
     */
    suspend fun patch(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toIntOrNull()

        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID.")
            return
        }

        val userPatch = call.receive<UserPatchDTO>()
        val patchedUser = service.patch(userId, userPatch)

        if (patchedUser == null) {
            call.respond(HttpStatusCode.NotFound, "User ID not found: $userId")
        } else {
            call.respond(HttpStatusCode.OK, patchedUser)
        }
    }

    /**
     * Handles DELETE request to delete a specific user.
     * Deletes the user if found and responds with appropriate status code.
     */
    suspend fun delete(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toIntOrNull()

        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID.")
            return
        }

        service.delete(userId)
        call.respond(HttpStatusCode.NoContent)
    }

    /**
     * Handles DELETE request to delete all users.
     * Deletes all users from the database and responds with an appropriate status code.
     */
    suspend fun deleteAll(call: ApplicationCall) {
        service.deleteAll()
        call.respond(HttpStatusCode.NoContent)
    }
}
