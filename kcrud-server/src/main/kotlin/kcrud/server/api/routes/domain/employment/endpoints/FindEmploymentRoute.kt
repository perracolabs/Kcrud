/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.routes.domain.employment.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcrud.server.api.routes.domain.employment.annotation.EmploymentRouteAPI
import kcrud.server.api.routes.domain.employment.getEmployeeId
import kcrud.server.api.routes.domain.employment.getEmploymentId
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.services.EmploymentService

@EmploymentRouteAPI
internal fun Route.findEmploymentByEmployeeId(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employments = service.findByEmployeeId(employeeId = employeeId)
        call.respond(message = employments)
    }
}

@EmploymentRouteAPI
internal fun Route.findEmploymentById(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        service.findById(employeeId = employeeId, employmentId = employmentId)?.also { employment ->
            call.respond(status = HttpStatusCode.OK, message = employment)
        } ?: EmploymentError.EmploymentNotFound(
            employeeId = employeeId,
            employmentId = employmentId
        ).raise()
    }
}
