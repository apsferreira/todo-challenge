package com.ojingo.register.data.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class CreateTodoTeamDTO {
	@NotNull
	@NotEmpty
	public String description;
}
