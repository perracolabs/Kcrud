/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.routes.domain.employment.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcrud.server.api.routes.domain.employment.annotation.EmploymentRouteAPI
import kcrud.server.api.routes.domain.employment.getEmployeeId
import kcrud.server.api.routes.domain.employment.getEmploymentId
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.services.EmploymentService

@EmploymentRouteAPI
internal fun Route.updateEmploymentById(service: EmploymentService) {
    put {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        val updatedEmployment = call.receive<EmploymentRequest>().let { employmentRequest ->
            service.update(employeeId = employeeId, employmentId = employmentId, employmentRequest = employmentRequest)
        }

        updatedEmployment?.also { employment ->
            call.respond(status = HttpStatusCode.OK, message = employment)
        } ?: EmploymentError.EmploymentNotFound(
            employeeId = employeeId,
            employmentId = employmentId
        ).raise()
    }
}
