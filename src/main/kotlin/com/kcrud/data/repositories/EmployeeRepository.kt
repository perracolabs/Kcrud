package com.kcrud.data.repositories

import com.kcrud.data.entities.ContactEntity
import com.kcrud.data.models.ContactTable
import com.kcrud.data.entities.EmployeeEntity
import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun findById(id: Int): EmployeeEntity? {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    EmployeeTable.contactId eq ContactTable.id
                }).select { EmployeeTable.id eq id }

            query.map { row ->
                rowToEntity(row = row)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntity> {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    EmployeeTable.contactId eq ContactTable.id
                }).selectAll()

            query.map { row ->
                rowToEntity(row = row)
            }
        }
    }

    override fun create(employee: EmployeeEntity): EmployeeEntity {
        return transaction {
            val newContactId = ContactTable.insert { dbContact ->
                dbContact[email] = employee.contact.email.trim()
                dbContact[phone] = employee.contact.phone.trim()
            } get ContactTable.id

            employee.contact.id = newContactId

            val newEmployeeId = EmployeeTable.insert { dbEmployee ->
                employeeToStatement(employee = employee, statement = dbEmployee)
                dbEmployee[contactId] = newContactId
            } get EmployeeTable.id

            findById(id = newEmployeeId)!!
        }
    }

    override fun update(id: Int, employee: EmployeeEntity): EmployeeEntity? {
        return transaction {
            // First, find the existing employee to get the contact id.
            val currentEmployee = findById(id) ?: return@transaction null
            val dbContactId = currentEmployee.contact.id!!
            employee.contact.id = dbContactId

            // Update the Contact table.
            ContactTable.update({ ContactTable.id eq dbContactId }) { dbContact ->
                dbContact[email] = employee.contact.email.trim()
                dbContact[phone] = employee.contact.phone.trim()
            }

            // Update the Employee table.
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
            contact = ContactEntity(
                id = row[ContactTable.id],
                email = row[ContactTable.email],
                phone = row[ContactTable.phone]
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
            this[EmployeeTable.contactId] = employee.contact.id!!
        }
    }
}
