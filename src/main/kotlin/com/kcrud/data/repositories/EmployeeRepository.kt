package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.data.models.EmployeeTable
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun create(employee: EmployeeEntity): EmployeeEntity {
        var generatedKey: Int? = null
        transaction {
            val insertStatement = EmployeeTable.insert { data ->
                entityToStatement(employee, data)
            }
            generatedKey = insertStatement[EmployeeTable.id]
        }
        return findById(generatedKey!!)!!
    }

    override fun findById(id: Int): EmployeeEntity? {
        return transaction {
            EmployeeTable.select { EmployeeTable.id eq id }.map { row ->
                rowToEntity(row)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntity> {
        return transaction {
            EmployeeTable.selectAll().map { row ->
                rowToEntity(row)
            }
        }
    }

    override fun update(id: Int, employee: EmployeeEntity): EmployeeEntity? {
        transaction {
            EmployeeTable.update({ EmployeeTable.id eq id }) { data ->
                entityToStatement(employee, data)
            }
        }
        return findById(id)
    }

    override fun patch(currentEmployee: EmployeeEntity, employeePatch: EmployeePatchDTO): EmployeeEntity? {
        val id = currentEmployee.id!!

        val newEmployeeData = EmployeeEntity(
            id = id,
            firstName = employeePatch.firstName ?: currentEmployee.firstName,
            lastName = employeePatch.lastName ?: currentEmployee.lastName,
            dob = employeePatch.dob ?: currentEmployee.dob
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

    private fun rowToEntity(row: ResultRow): EmployeeEntity {
        return EmployeeEntity(
            id = row[EmployeeTable.id],
            firstName = row[EmployeeTable.firstName],
            lastName = row[EmployeeTable.lastName],
            dob = row[EmployeeTable.dob].toKotlinLocalDate()
        )
    }

    private fun entityToStatement(employee: EmployeeEntity, statement: UpdateBuilder<Int>) {
        statement[EmployeeTable.firstName] = employee.firstName
        statement[EmployeeTable.lastName] = employee.lastName
        statement[EmployeeTable.dob] = employee.dob.toJavaLocalDate()
    }
}
