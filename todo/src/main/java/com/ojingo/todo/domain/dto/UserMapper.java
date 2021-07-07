package com.ojingo.todo.domain.dto;

import org.mapstruct.Mapper;

import com.ojingo.todo.domain.entities.User;

@Mapper(componentModel = "cdi")
public interface UserMapper {
	User convertToUser(UserDTO userDTO);
	
	UserDTO convertToUserDTO(User user);
}
