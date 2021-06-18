package com.ojingo.register.data.dto;

import org.mapstruct.Mapper;

import com.ojingo.register.domain.models.User;

@Mapper(componentModel = "cdi")
public interface UserMapper {
	User convertToUser(UserDTO userDTO);
	
	User convertToUser(CreateUserDTO createUserDTO);
	
	User convertToUser(UpdateUserDTO updateUserDTO);
	
	UserDTO convertToUserDTO(User user);
}
