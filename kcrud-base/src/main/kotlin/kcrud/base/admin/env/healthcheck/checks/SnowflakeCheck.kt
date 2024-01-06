/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.env.healthcheck.checks

import kcrud.base.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.base.admin.env.security.snowflake.SnowflakeData
import kcrud.base.admin.env.security.snowflake.SnowflakeFactory
import kotlinx.serialization.Serializable

@HealthCheckAPI
@Serializable
data class SnowflakeCheck(
    val errors: MutableList<String> = mutableListOf(),
    var testId: String? = null,
    var testResult: SnowflakeData? = null,
    val timestampEpoch: Long = SnowflakeFactory.timestampEpoch,
    val nanoTimeStart: Long = SnowflakeFactory.nanoTimeStart,
) {
    init {
        // Generating testId and handling potential exceptions.
        testId = try {
            SnowflakeFactory.nextId()
        } catch (ex: Exception) {
            errors.add("${SnowflakeCheck::class.simpleName}. Error generating snowflake. ${ex.message}")
            "error"
        }

        // Parsing testResult and handling potential exceptions.
        testResult = try {
            SnowflakeFactory.parse(id = testId!!)
        } catch (ex: Exception) {
            errors.add("${SnowflakeCheck::class.simpleName}. Unable to parse testId '$testId'. ${ex.message}")
            null
        }
    }
}