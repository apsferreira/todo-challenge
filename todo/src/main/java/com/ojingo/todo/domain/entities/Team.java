package com.ojingo.todo.domain.entities;

import java.util.UUID;

public class Team {
	
	private UUID id;
	
	private String name;
	
	private Long originalId; 

	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Long getOriginalId() {
		return originalId;
	}

	public void setOriginalId(Long originalId) {
		this.originalId = originalId;
	}

	
	public static Team of (UUID id, String name, Long originalId) {
		Team team = new Team();
		
		team.id = id;
		team.name = name;
		team.originalId = originalId;
		
		return team;
	}
	
	public static Team of (UUID id, String name) {
		Team team = new Team();
		
		team.id = id;
		team.name = name;
		
		return team;
	}
	
	public static Team of (String name, Long originalId) {
		Team team = new Team();
		
		team.name = name;
		team.originalId = originalId;
		
		return team;
	}
	
	public static Team of (String name) {
		Team team = new Team();
		
		team.name = name;
		
		return team;
	}
}
