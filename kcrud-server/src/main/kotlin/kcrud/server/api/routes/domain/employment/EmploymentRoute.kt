/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.routes.domain.employment

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.data.utils.toUUIDOrNull
import kcrud.server.api.routes.domain.employee.employeeRoute
import kcrud.server.api.routes.domain.employment.annotation.EmploymentRouteAPI
import kcrud.server.api.routes.domain.employment.endpoints.*
import kcrud.server.domain.services.EmploymentService
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Employment endpoints.
 *
 * These endpoints are segmented in multiple functions/files
 * to demonstrate how to organize routes separately.
 *
 * See [Application Structure](https://ktor.io/docs/structuring-applications.html) for examples
 * of how to organize routes in diverse ways.
 *
 * See [Route.employeeRoute] for an example of how to define multiple routes in a single function.
 */
@OptIn(EmploymentRouteAPI::class)
fun Route.employmentRoute() {
    val service by inject<EmploymentService>()

    route("${AppSettings.deployment.apiVersion}/employees/{employee_id}/employments") {

        createEmployment(service)
        findEmploymentByEmployeeId(service)
        deleteEmploymentByEmployeeId(service)

        route("{employment_id}") {
            findEmploymentById(service)
            updateEmploymentById(service)
            deleteEmploymentById(service)
        }
    }
}

@EmploymentRouteAPI
fun ApplicationCall.getEmployeeId(): UUID {
    return parameters["employee_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}

@EmploymentRouteAPI
fun ApplicationCall.getEmploymentId(): UUID {
    return parameters["employment_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employment ID argument.")
}
