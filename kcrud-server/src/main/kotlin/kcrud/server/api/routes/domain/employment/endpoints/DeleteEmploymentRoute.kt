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
import kcrud.server.domain.services.EmploymentService

@EmploymentRouteAPI
internal fun Route.deleteEmploymentByEmployeeId(service: EmploymentService) {
    delete {
        val employeeId = call.getEmployeeId()
        service.deleteAll(employeeId = employeeId).also { deletedCount ->
            call.respond(status = HttpStatusCode.OK, message = deletedCount)
        }
    }
}

@EmploymentRouteAPI
internal fun Route.deleteEmploymentById(service: EmploymentService) {
    delete {
        val employmentId = call.getEmploymentId()
        service.delete(employmentId = employmentId).also { deletedCount ->
            call.respond(status = HttpStatusCode.OK, message = deletedCount)
        }
    }
}
