package com.ojingo.register.domain.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class CreateTodoTeamDTO {

	@NotNull
	@NotEmpty
	public String name;
}
