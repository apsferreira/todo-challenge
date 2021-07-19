package com.ojingo.todo.domain.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Note {
	
	private UUID id;

	private String description;
	
	private boolean done;

	private boolean favorite;
		
	private User user;
	
	private Todo todo;
	
	private String subjectJWT;
	
	private OffsetDateTime createdAt;
	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Todo getTodo() {
		return todo;
	}

	public void setTodo(Todo todo) {
		this.todo = todo;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getSubjectJWT() {
		return subjectJWT;
	}

	public void setSubjectJWT(String subjectJWT) {
		this.subjectJWT = subjectJWT;
	}

	
	public Note(){}

	public Note(UUID id, String description, User user, Todo todo) {
		this.id = id;
		this.description = description;
		this.user = user;		
		this.todo = todo;
	}
	
	public static Note of(UUID id, String description, boolean done, boolean favorite, OffsetDateTime createdAt, User user, Todo todo, String subjectJWT) {
		Note note = new Note();
		note.id = id;
		note.description = description;
		note.done = done;
		note.favorite = favorite;
		note.createdAt = createdAt;
		note.user = user;
		note.todo = todo;
		note.subjectJWT = subjectJWT;
		
		return note;
	}
	
	public static Note of(UUID id, String description) {
		Note note = new Note();
		note.id = id;
		note.description = description;
		
		return note;
	}
	
	public static Note of(UUID id) {
		Note note = new Note();
		
		note.id = id;
		
		return note;
	}
}
