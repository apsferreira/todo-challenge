package com.ojingo.todo.domain.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class CreateTodoDTO {
	@NotEmpty
	@NotBlank
	public String description;
	
	public UUID teamId;
	
	public UUID userId;
}
