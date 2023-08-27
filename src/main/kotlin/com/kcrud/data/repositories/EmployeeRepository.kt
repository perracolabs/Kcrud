package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeEntityIn
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun create(employee: EmployeeEntityIn): EmployeeEntity {
        return transaction {
            val newEmployeeId = EmployeeTable.insert { dbEmployee ->
                entityToStatement(employee = employee, statement = dbEmployee)
            } get EmployeeTable.id
            findById(id = newEmployeeId)!!
        }
    }

    override fun findById(id: Int): EmployeeEntity? {
        return transaction {
            EmployeeTable.select { EmployeeTable.id eq id }.map { row ->
                rowToEntity(row = row)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntity> {
        return transaction {
            EmployeeTable.selectAll().map { row ->
                rowToEntity(row = row)
            }
        }
    }

    override fun update(id: Int, employee: EmployeeEntityIn): EmployeeEntity? {
        return transaction {
            EmployeeTable.update({ EmployeeTable.id eq id }) { dbEmployee ->
                entityToStatement(employee = employee, statement = dbEmployee)
            }
            findById(id = id)
        }
    }

    override fun patch(id: Int, employeePatch: EmployeePatchDTO): EmployeeEntity? {
        return findById(id)?.let { currentEmployeeData ->
            val newEmployeeData = EmployeeEntityIn(
                firstName = employeePatch.firstName ?: currentEmployeeData.firstName,
                lastName = employeePatch.lastName ?: currentEmployeeData.lastName,
                dob = employeePatch.dob ?: currentEmployeeData.dob
            )
            update(id = id, employee = newEmployeeData)
        }
    }

    override fun delete(id: Int) {
        transaction {
            EmployeeTable.deleteWhere { EmployeeTable.id eq id }
        }
    }

    override fun deleteAll() {
        transaction {
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
            dob = row[EmployeeTable.dob]
        )
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmployeeEntityIn] instance.
     */
    private fun entityToStatement(employee: EmployeeEntityIn, statement: UpdateBuilder<Int>) {
        statement[EmployeeTable.firstName] = employee.firstName.trim()
        statement[EmployeeTable.lastName] = employee.lastName.trim()
        statement[EmployeeTable.dob] = employee.dob
    }
}
