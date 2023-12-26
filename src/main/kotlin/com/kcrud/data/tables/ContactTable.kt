/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.tables

import com.kcrud.data.utils.EncryptionUtils
import org.jetbrains.exposed.crypt.encryptedVarchar
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/**
 * Database table definition for employee contact details.
 *
 * Demonstrates how to encrypt data in the database.
 *
 * For encrypted fields, the lengths is larger than the actual length of the data,
 * since the encrypted data will be larger than the original value.
 */
internal object ContactTable : Table(name = "contact") {
    private val encryptor = EncryptionUtils.getEncryptor()

    val id = uuid(name = "contact_id").autoGenerate()
    val employeeId = uuid(name = "employee_id").references(ref = EmployeeTable.id, onDelete = ReferenceOption.CASCADE)
    val email = encryptedVarchar(name = "email", cipherTextLength = encryptor.maxColLength(64), encryptor = encryptor)
    val phone = encryptedVarchar(name = "phone", cipherTextLength = encryptor.maxColLength(12), encryptor = encryptor)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Contact_ID")
}
