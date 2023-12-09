/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.employment.EmploymentInput
import com.kcrud.services.EmploymentService
import com.kcrud.utils.SettingsProvider
import com.kcrud.utils.toUUIDOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


/**
 * Employment related endpoint configurations.
 */
class EmploymentRouting(private val routingNode: Route) {

    fun configure() {
        routingNode.route(EmployeeRouting.API_VERSION) {
            if (SettingsProvider.get.jwt.isEnabled) {
                authenticate {
                    setupRoutes(node = this)
                }
            } else {
                setupRoutes(node = this)
            }
        }
    }

    private fun setupRoutes(node: Route) {
        node.apply {
            val service by inject<EmploymentService>()

            route(EmployeeRouting.EMPLOYEE_ROUTE) {

                route("{${EmployeeRouting.EMPLOYEE_PATH_PARAMETER}}") {

                    route(EMPLOYMENTS_ROUTE) {

                        create(node = this, service = service)
                        findByEmployeeId(node = this, service = service)

                        route("{$EMPLOYMENT_PATH_PARAMETER}") {

                            findById(node = this, service = service)
                            update(node = this, service = service)
                            delete(node = this, service = service)
                        }
                    }
                }
            }
        }
    }

    private fun create(node: Route, service: EmploymentService) {
        node.apply {
            post {
                val employeeId = call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()

                employeeId?.let {
                    val employment = call.receive<EmploymentInput>()
                    val newEmployment = service.create(employeeId, employment)
                    call.respond(HttpStatusCode.Created, newEmployment)
                } ?: call.respond(HttpStatusCode.NotFound, "Employee not found.")
            }
        }
    }

    private fun findByEmployeeId(node: Route, service: EmploymentService) {
        node.apply {
            get {
                val employeeId = call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()

                employeeId?.let {
                    val employments = service.findByEmployeeId(employeeId)
                    call.respond(employments)
                } ?: call.respond(HttpStatusCode.NotFound, "Employee not found.")
            }
        }
    }

    private fun findById(node: Route, service: EmploymentService) {
        node.apply {
            get {
                call.parameters[EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->
                    service.findById(employmentId)?.also { employment ->
                        call.respond(employment)
                    } ?: call.respond(HttpStatusCode.NotFound, "Employment ID not found: $employmentId")
                } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employment ID argument.")
            }
        }
    }

    private fun update(node: Route, service: EmploymentService) {
        node.apply {
            put {
                call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()?.let { employeeId ->

                    call.parameters[EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->

                        val updatedEmployment = call.receive<EmploymentInput>().run {
                            service.update(employeeId = employeeId, employmentId = employmentId, employment = this)
                        }

                        updatedEmployment?.also { employment ->
                            call.respond(status = HttpStatusCode.OK, message = employment)
                        } ?: call.respond(status = HttpStatusCode.NotFound, message = "Employment ID not found: $employmentId")

                    } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID argument.")
                } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employee ID argument.")
            }
        }
    }

    private fun delete(node: Route, service: EmploymentService) {
        node.apply {
            delete {
                call.parameters[EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->
                    service.delete(employmentId)
                    call.respond(HttpStatusCode.NoContent)
                } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID.")
            }
        }
    }

    companion object {
        private const val EMPLOYMENTS_ROUTE = "employments"
        const val EMPLOYMENT_PATH_PARAMETER = "employment_id"
    }
}
