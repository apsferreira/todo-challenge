package com.ojingo.register.domain.dto;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class UpdateTodoDTO {
	@NotNull
	@NotEmpty
	public String description;
	
	public Long idUser;
	
	public Long idTeam;
}
