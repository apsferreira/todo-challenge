package com.ojingo.register.data.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

public class CreateTeamDTO {

	@NotNull
	@NotEmpty
	@Size(min=3, max=10)
	public String name;
	
	public String key;
	
	public Long idTeam; 
}
