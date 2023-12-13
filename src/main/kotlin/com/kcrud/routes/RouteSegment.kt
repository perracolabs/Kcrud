/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

/**
 * Defines the routing segments and path parameters for the API,
 * centralizing the definition of the main routes and path parameters
 * used throughout the application, ensuring consistency and ease of maintenance.
 */
internal object RouteSegment {
    /** The version segment of the API route. */
    const val API_VERSION = "v1"

    /** Contains constants related to employee routing. */
    object Employee {
        /** The route segment for accessing employee-related operations. */
        const val ROUTE = "employees"

        /** The path parameter name for identifying a specific employee. */
        const val EMPLOYEE_ID = "employee_id"

        /**
         * The path template for accessing a specific employee.
         * Incorporates the employee ID as a path parameter.
         */
        const val EMPLOYEE_ID_PATH = "{${EMPLOYEE_ID}}"
    }

    /** Contains constants related to employment routing. */
    object Employment {
        /** The route segment for accessing employment-related operations. */
        const val ROUTE = "employments"

        /** The path parameter name for identifying a specific employment record. */
        const val EMPLOYMENT_ID = "employment_id"

        /**
         * The path template for accessing a specific employment record.
         * Incorporates the employment ID as a path parameter.
         */
        const val EMPLOYMENT_ID_PATH = "{${EMPLOYMENT_ID}}"
    }
}
