package com.ojingo.register.domain.dto;

import com.sun.istack.NotNull;

public class CreateTodoDTO {

	@NotNull
	public String description;
	
	public Long idUser;
	
	public Long idTeam;
}
