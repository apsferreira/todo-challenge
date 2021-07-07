package com.ojingo.register.domain.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class CreateUserDTO {

	@NotNull
	@NotEmpty
 	public String name;

	@NotNull
	@NotEmpty
	public String email;
	
	@NotNull
	@NotEmpty
	public Long idTeam;
}
