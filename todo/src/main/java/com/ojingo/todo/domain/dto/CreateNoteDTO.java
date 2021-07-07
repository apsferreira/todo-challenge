package com.ojingo.todo.domain.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class CreateNoteDTO {
	@NotBlank
	@NotEmpty
	public String description;
	
	@NotBlank
	@NotEmpty
	public UUID userId;
	
	@NotBlank
	@NotEmpty
	public UUID todoId;
}
