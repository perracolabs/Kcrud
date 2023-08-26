package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class EmployeeRepository : IEmployeeRepository {

    override fun create(employee: EmployeeEntity): EmployeeEntity {
        var generatedKey: Int? = null
        transaction {
            val insertStatement = EmployeeTable.insert { data ->
                data[name] = employee.name
                data[age] = employee.age
            }
            generatedKey = insertStatement[EmployeeTable.id]
        }
        return findById(generatedKey!!)!!
    }

    override fun findById(id: Int): EmployeeEntity? {
        return transaction {
            EmployeeTable.select { EmployeeTable.id eq id }.map { data ->
                EmployeeEntity(
                    id = data[EmployeeTable.id],
                    name = data[EmployeeTable.name],
                    age = data[EmployeeTable.age]
                )
            }.singleOrNull()
        }
    }

    override fun findAll(): List<EmployeeEntity> {
        return transaction {
            EmployeeTable.selectAll().map { row ->
                EmployeeEntity(
                    id = row[EmployeeTable.id],
                    name = row[EmployeeTable.name],
                    age = row[EmployeeTable.age]
                )
            }
        }
    }

    override fun update(id: Int, employee: EmployeeEntity): EmployeeEntity? {
        transaction {
            EmployeeTable.update({ EmployeeTable.id eq id }) { data ->
                data[name] = employee.name
                data[age] = employee.age
            }
        }
        return findById(id)
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
}
