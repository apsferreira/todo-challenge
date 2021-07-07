package com.ojingo.todo.domain.entities;

import java.util.UUID;

public class Todo {
	private UUID id;
	
	private User user;
	
	private Team team;
	
	private String description;
	

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Todo(){}

	public Todo(UUID id, String description, User user) {
		this.id = id;
		this.description = description;
		this.user = user;		
	}
	
	public Todo(UUID id, String description, Team team) {
		this.id = id;
		this.description = description;	
		this.team = team;
	}
	
	public static Todo of(UUID id, String description, User user, Team team) {
		Todo todo = new Todo();
		todo.id = id;
		todo.description = description;
		todo.user = user;
		todo.team = team;
		
		return todo;
	}
	
	public static Todo of(UUID id, String description) {
		Todo todo = new Todo();
		todo.id = id;
		todo.description = description;
		
		return todo;
	}

	public static Todo of(UUID id) {
		Todo todo = new Todo();
		todo.id = id;
			
		return todo;
	}
}
