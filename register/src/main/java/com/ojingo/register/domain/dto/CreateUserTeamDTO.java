package com.ojingo.register.domain.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class CreateUserTeamDTO {

	@NotNull
	@NotEmpty
	public String username;
	
	@NotNull
	@NotEmpty
	public String email;
}
