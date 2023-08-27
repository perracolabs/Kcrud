package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntityIn
import com.kcrud.data.models.EmployeeEntityOut
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun create(employee: EmployeeEntityIn): EmployeeEntityOut {
        var generatedKey: Int? = null
        transaction {
            val insertStatement = EmployeeTable.insert { data ->
                entityToStatement(employee, data)
            }
            generatedKey = insertStatement[EmployeeTable.id]
        }
        return findById(generatedKey!!)!!
    }

    override fun findById(id: Int): EmployeeEntityOut? {
        return transaction {
            EmployeeTable.select { EmployeeTable.id eq id }.map { row ->
                rowToEntity(row)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntityOut> {
        return transaction {
            EmployeeTable.selectAll().map { row ->
                rowToEntity(row)
            }
        }
    }

    override fun update(id: Int, employee: EmployeeEntityIn): EmployeeEntityOut? {
        transaction {
            EmployeeTable.update({ EmployeeTable.id eq id }) { data ->
                entityToStatement(employee, data)
            }
        }
        return findById(id)
    }

    override fun patch(id: Int, employeePatch: EmployeePatchDTO): EmployeeEntityOut? {
        val currentEmployeeData = findById(id) ?: return null

        val newEmployeeData = EmployeeEntityIn(
            firstName = employeePatch.firstName ?: currentEmployeeData.firstName,
            lastName = employeePatch.lastName ?: currentEmployeeData.lastName,
            dob = employeePatch.dob ?: currentEmployeeData.dob
        )

        return update(id, newEmployeeData)
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

    private fun rowToEntity(row: ResultRow): EmployeeEntityOut {
        return EmployeeEntityOut(
            id = row[EmployeeTable.id],
            firstName = row[EmployeeTable.firstName],
            lastName = row[EmployeeTable.lastName],
            dob = row[EmployeeTable.dob]
        )
    }

    private fun entityToStatement(employee: EmployeeEntityIn, statement: UpdateBuilder<Int>) {
        statement[EmployeeTable.firstName] = employee.firstName.trim()
        statement[EmployeeTable.lastName] = employee.lastName.trim()
        statement[EmployeeTable.dob] = employee.dob
    }
}
