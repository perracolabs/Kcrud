/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.domain.entities.employment.types

import com.kcrud.core.data.utils.EnumWithId

/**
 * Example in which each item has an id,
 * which is the actual value that will be stored in the database,
 * instead of the name of the enum item.
 */
enum class WorkModality(override val id: Int) : EnumWithId {
    ON_SITE(100),
    REMOTE(101),
    HYBRID(102);

    companion object {
        private val map = WorkModality.entries.associateBy(WorkModality::id)
        fun fromId(id: Int): WorkModality? = map[id]
    }
}
