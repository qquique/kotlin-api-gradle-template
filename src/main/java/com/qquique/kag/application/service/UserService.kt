package com.qquique.kag.application.service

import com.qquique.kag.application.dto.UserDTO
import com.qquique.kag.application.mapper.UserMapper
import com.qquique.kag.infrastructure.database.repository.UserRepositoryImpl
import io.github.oshai.kotlinlogging.KotlinLogging

class UserService(private val repository: UserRepositoryImpl) {
    private val logger = KotlinLogging.logger {}

    fun getAllUsers(): List<UserDTO> {
        return try {
            repository.findAll()
                .map(UserMapper.INSTANCE::mapToDTO)
        } catch (e: Exception) {
            logger.error { "error getting users" }
            emptyList()
        }
    }

    fun getUserById(id: Long): UserDTO? {
        return try {
            repository.findById(id)
                .map(UserMapper.INSTANCE::mapToDTO)
                .orElse(null)
        } catch (e: Exception) {
            logger.error { "Error retrieving user with ID $id, $e" }
            null
        }
    }

    fun createUser(userDTO: UserDTO): UserDTO {
        return try {
            val userEntity = UserMapper.INSTANCE.mapToDomain(userDTO)
            val savedUser = repository.save(userEntity)
            UserMapper.INSTANCE.mapToDTO(savedUser)
        } catch (e: Exception) {
            logger.error { "Error creating user : $e" }
            throw e
        }
    }

    fun updateUser(id: Long, userDTO: UserDTO): UserDTO? {
        return try {
            repository.findById(id).map { user ->
                UserMapper.INSTANCE.updateUserFromDTO(userDTO, user)
                val updatedUser = repository.save(user)
                UserMapper.INSTANCE.mapToDTO(updatedUser)
            }.orElse(null)
        } catch (e: Exception) {
            logger.error { "Error updating user with ID $id, $e" }
            null
        }
    }

    fun deleteUser(id: Long) {
        try {
            repository.deleteById(id)
        } catch (e: Exception) {
            logger.error { "Error deleting user with ID $id, $e" }
        }
    }
}