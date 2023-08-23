package com.kcrud.services

import com.kcrud.data.models.UserEntity
import com.kcrud.data.models.UserPatchDTO
import com.kcrud.data.repositories.IUserRepository
import org.koin.core.component.KoinComponent


class UserService(private val repository: IUserRepository) : KoinComponent {

    /**
     * Creates a new user in the system.
     * @param user The user entity to be created.
     * @return The created user entity with generated ID.
     */
    fun create(user: UserEntity): UserEntity {
        return repository.create(user)
    }

    /**
     * Retrieves a user by its ID.
     * @param id The ID of the user to be retrieved.
     * @return The user entity if found, null otherwise.
     */
    fun findById(id: Int): UserEntity? {
        return repository.findById(id)
    }

    /**
     * Retrieves all users in the system.
     * @return List of all user entities.
     */
    fun findAll(): List<UserEntity> {
        return repository.findAll()
    }

    /**
     * Updates a user's details in the system.
     * @param id The ID of the user to be updated.
     * @param user The new details for the user.
     * @return The updated user entity if the update was successful, null otherwise.
     */
    fun update(id: Int, user: UserEntity): UserEntity? {
        return repository.update(id, user)
    }

    /**
     * Partially updates an existing user in the system.
     * @param userPatch The user data to be updated.
     * @return The updated user.
     */
    fun patch(id: Int, userPatch: UserPatchDTO): UserEntity? {
        val currentUser = findById(id) ?: return null
        val updatedUser = UserEntity(
            id = currentUser.id,
            name = userPatch.name ?: currentUser.name,
            age = userPatch.age ?: currentUser.age
        )
        return repository.update(id, updatedUser)
    }

    /**
     * Deletes a user from the system using the provided ID.
     * @param id The ID of the user to be deleted.
     */
    fun delete(id: Int) {
        repository.delete(id)
    }

    /**
     * Deletes all users from the system.
     */
    fun deleteAll() {
        repository.deleteAll()
    }
}
