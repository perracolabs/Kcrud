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
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.services.EmploymentService

@EmploymentRouteAPI
internal fun Route.createEmployment(service: EmploymentService) {
    post {
        val employeeId = call.getEmployeeId()
        val employmentRequest = call.receive<EmploymentRequest>()
        val newEmployment = service.create(employeeId = employeeId, employmentRequest = employmentRequest)
        call.respond(status = HttpStatusCode.Created, message = newEmployment)
    }
}
