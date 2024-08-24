package com.qquique.kag.application.mapper

import com.qquique.kag.application.dto.UserDTO
import com.qquique.kag.domain.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper
interface UserMapper {
    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
    fun mapToDTO(user: User) : UserDTO
    @Mapping(target="lastDateCreated", ignore=true)
    @Mapping(target="lastDateModified", ignore=true)
    fun mapToDomain(userDTO: UserDTO): User
    fun updateUserFromDTO(userDTO: UserDTO, @MappingTarget user: User)
}