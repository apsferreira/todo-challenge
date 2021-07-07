package com.ojingo.todo.domain.entities;

import java.util.UUID;

public class User  {	
	private UUID id;
	
	private Team team;
	
	private String username;
	
	private String email;
	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static User of(UUID id, String username, String email, Team team){
		User user = new User();
		
		user.id = id;
		user.username = username;
		user.email = email;
		user.team = team;
		
		return user;
	}
	
	public static User of(UUID id, String username, String email){
		User user = new User();
		
		user.id = id;
		user.username = username;	
		user.email = email;	
		
		return user;
	}
	
	public static User of(UUID id, String username){
		User user = new User();
		
		user.id = id;
		user.username = username;
		
		return user;
	}
	
	public static User of(UUID id){
		User user = new User();
		
		user.id = id;
		
		return user;
	}
}
