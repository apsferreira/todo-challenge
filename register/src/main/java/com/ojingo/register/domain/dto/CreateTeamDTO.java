package com.ojingo.register.domain.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class CreateTeamDTO {

	@NotNull
	@NotEmpty
	public String name; 
}
