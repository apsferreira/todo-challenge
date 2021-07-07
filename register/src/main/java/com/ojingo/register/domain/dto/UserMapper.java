package com.ojingo.register.domain.dto;

import org.mapstruct.Mapper;

import com.ojingo.register.domain.entities.User;

@Mapper(componentModel = "cdi")
public interface UserMapper {
	User convertToUser(UserDTO userDTO);
	
	User convertToUser(CreateUserDTO createUserDTO);
	
	User convertToUser(CreateUserTeamDTO createUserDTO);
	
	User convertToUser(UpdateUserDTO updateUserDTO);
	
	UserDTO convertToUserDTO(User user);
}
