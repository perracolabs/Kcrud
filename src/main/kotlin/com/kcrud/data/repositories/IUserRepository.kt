package com.kcrud.data.repositories

import com.kcrud.data.models.UserEntity


interface IUserRepository {

    /**
     * Creates a new user in the database and returns the created user entity.
     * @param user The user entity to be created.
     * @return The created user entity with generated ID.
     */
    fun create(user: UserEntity): UserEntity

    /**
     * Retrieves a user entity by its ID from the database.
     * @param id The ID of the user to be retrieved.
     * @return The user entity if found, null otherwise.
     */
    fun findById(id: Int): UserEntity?

    /**
     * Retrieves all user entities from the database.
     * @return List of all user entities.
     */
    fun findAll(): List<UserEntity>

    /**
     * Updates a user's details in the database using the provided ID and user entity.
     * @param id The ID of the user to be updated.
     * @param user The new details for the user.
     * @return The updated user entity if the update was successful, null otherwise.
     */
    fun update(id: Int, user: UserEntity): UserEntity?

    /**
     * Deletes a user from the database using the provided ID.
     * @param id The ID of the user to be deleted.
     */
    fun delete(id: Int)

    /**
     * Deletes all users from the database.
     */
    fun deleteAll()
}

