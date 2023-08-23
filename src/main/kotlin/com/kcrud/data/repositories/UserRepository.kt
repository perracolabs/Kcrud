package com.kcrud.data.repositories

import com.kcrud.data.models.UserEntity
import com.kcrud.data.models.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class UserRepository : IUserRepository {

    override fun create(user: UserEntity): UserEntity {
        var generatedKey: Int? = null
        transaction {
            val insertStatement = UserTable.insert { data ->
                data[name] = user.name
                data[age] = user.age
            }
            generatedKey = insertStatement[UserTable.id]
        }
        return findById(generatedKey!!)!!
    }

    override fun findById(id: Int): UserEntity? {
        return transaction {
            UserTable.select { UserTable.id eq id }.map { data ->
                UserEntity(
                    id = data[UserTable.id],
                    name = data[UserTable.name],
                    age = data[UserTable.age]
                )
            }.singleOrNull()
        }
    }

    override fun findAll(): List<UserEntity> {
        return transaction {
            UserTable.selectAll().map { row ->
                UserEntity(
                    id = row[UserTable.id],
                    name = row[UserTable.name],
                    age = row[UserTable.age]
                )
            }
        }
    }

    override fun update(id: Int, user: UserEntity): UserEntity? {
        transaction {
            UserTable.update({ UserTable.id eq id }) { data ->
                data[name] = user.name
                data[age] = user.age
            }
        }
        return findById(id)
    }

    override fun delete(id: Int) {
        transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
    }

    override fun deleteAll() {
        transaction {
            UserTable.deleteAll()
        }
    }
}
