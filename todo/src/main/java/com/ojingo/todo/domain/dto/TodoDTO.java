package com.ojingo.todo.domain.dto;

import java.util.UUID;

import io.vertx.mutiny.sqlclient.Row;

public class TodoDTO {
	public UUID id;
	
	public String description;
	
	public UserDTO user;
	
	public TeamDTO team;
	
	
	public static TodoDTO from(Row row) {
		TodoDTO todoDTO = new TodoDTO();
		
		todoDTO.id = row.getUUID("id");
		todoDTO.description = row.getString("description");
		
		return todoDTO;
	}
}
