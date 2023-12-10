/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employee

import com.kcrud.utils.EnumWithId

/**
 * Example in which each item has an id,
 * which is the actual value that will be stored in the database,
 * instead of the name of the enum item.
 */
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
