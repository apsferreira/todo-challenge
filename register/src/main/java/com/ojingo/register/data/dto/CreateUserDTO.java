package com.ojingo.register.data.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

public class CreateUserDTO {

	@NotNull
	@NotEmpty
	@Size(min=3, max=10)
	public String name;
	
	@NotNull
	@NotEmpty
	public Long idTeam;
}
