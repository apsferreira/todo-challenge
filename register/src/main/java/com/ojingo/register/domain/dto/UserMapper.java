package com.ojingo.register.domain.dto;

import org.mapstruct.Mapper;

import com.ojingo.register.domain.entities.User;

@Mapper(componentModel = "cdi")
public interface UserMapper {
	User convertToUser(UserDTO userDTO);
	
	UserDTO convertToUserDTO(User user);
}
