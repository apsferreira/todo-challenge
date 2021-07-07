package com.ojingo.todo.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NoteDTO {
	public UUID id;
	
	public String description;
	
	public boolean done;
	
	public boolean favorite;
	
	public TodoDTO todo;
	
	public UserDTO user;
	
	public OffsetDateTime createdAt;	
}
