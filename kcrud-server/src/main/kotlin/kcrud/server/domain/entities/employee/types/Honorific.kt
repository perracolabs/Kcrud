/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employee.types

import kcrud.base.data.utils.EnumWithId
import kotlinx.serialization.Serializable

/**
 * Example in which each item has an id,
 * which is the actual value that will be stored in the database,
 * instead of the name of the enum item.
 */
@Serializable
enum class Honorific(override val id: Int) : EnumWithId {
    MR(100),
    MRS(101),
    MS(102),
    DR(103),
    MISS(104),
    UNKNOWN(105);

    companion object {
        private val map = Honorific.entries.associateBy(Honorific::id)
        fun fromId(id: Int): Honorific? = map[id]
    }
}
