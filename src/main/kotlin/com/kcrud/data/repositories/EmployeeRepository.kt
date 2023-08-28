package com.kcrud.data.repositories

import com.kcrud.data.models.ContactDetailsEntity
import com.kcrud.data.models.ContactDetailsTable
import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun findById(id: Int): EmployeeEntity? {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactDetailsTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    EmployeeTable.contactDetailsId eq ContactDetailsTable.id
                }).select { EmployeeTable.id eq id }

            query.map { row ->
                rowToEntity(row = row)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntity> {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactDetailsTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    EmployeeTable.contactDetailsId eq ContactDetailsTable.id
                }).selectAll()

            query.map { row ->
                rowToEntity(row = row)
            }
        }
    }

    override fun create(employee: EmployeeEntity): EmployeeEntity {
        return transaction {
            val newContactDetailsId = ContactDetailsTable.insert { dbContact ->
                dbContact[email] = employee.contactDetails.email.trim()
                dbContact[phone] = employee.contactDetails.phone.trim()
            } get ContactDetailsTable.id

            employee.contactDetails.id = newContactDetailsId

            val newEmployeeId = EmployeeTable.insert { dbEmployee ->
                employeeToStatement(employee = employee, statement = dbEmployee)
                dbEmployee[contactDetailsId] = newContactDetailsId
            } get EmployeeTable.id

            findById(id = newEmployeeId)!!
        }
    }

    override fun update(id: Int, employee: EmployeeEntity): EmployeeEntity? {
        return transaction {
            // First, find the existing employee to get the contactDetailsId.
            val currentEmployee = findById(id) ?: return@transaction null
            val dbContactDetailsId = currentEmployee.contactDetails.id!!
            employee.contactDetails.id = dbContactDetailsId

            // Update ContactDetailsTable.
            ContactDetailsTable.update({ ContactDetailsTable.id eq dbContactDetailsId }) { dbContactDetails ->
                dbContactDetails[email] = employee.contactDetails.email.trim()
                dbContactDetails[phone] = employee.contactDetails.phone.trim()
            }

            // Update EmployeeTable.
            EmployeeTable.update({ EmployeeTable.id eq id }) { dbEmployee ->
                employeeToStatement(employee = employee, statement = dbEmployee)
            }

            findById(id = id)
        }
    }

    override fun delete(id: Int): Int {
        return transaction {
            EmployeeTable.deleteWhere { EmployeeTable.id eq id }
        }
    }

    override fun deleteAll(): Int {
        return transaction {
            EmployeeTable.deleteAll()
        }
    }

    /**
     * Converts a database [ResultRow] to an [EmployeeEntity] object.
     */
    private fun rowToEntity(row: ResultRow): EmployeeEntity {
        return EmployeeEntity(
            id = row[EmployeeTable.id],
            firstName = row[EmployeeTable.firstName],
            lastName = row[EmployeeTable.lastName],
            dob = row[EmployeeTable.dob],
            contactDetails = ContactDetailsEntity(
                id = row[ContactDetailsTable.id],
                email = row[ContactDetailsTable.email],
                phone = row[ContactDetailsTable.phone]
            )
        )
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmployeeEntity] instance.
     */
    private fun employeeToStatement(employee: EmployeeEntity, statement: UpdateBuilder<Int>) {
        with(statement) {
            this[EmployeeTable.firstName] = employee.firstName.trim()
            this[EmployeeTable.lastName] = employee.lastName.trim()
            this[EmployeeTable.dob] = employee.dob
            this[EmployeeTable.contactDetailsId] = employee.contactDetails.id!!
        }
    }
}
