package com.ojingo.todo.presentation.services;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.data.repositories.TodoRepository;
import com.ojingo.todo.domain.entities.Todo;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class TodoService {
	
	@Inject
	private TodoRepository todoRepository;
	
	public Uni<UUID> create(Todo todo){		
		if (todo.getUser().getId() != null && todo.getTeam().getId() != null) {
			return Uni.createFrom().item(() -> null);
		}
		
		if(todo.getUser().getId() != null) {
			return todoRepository.createByUser(todo);
		}
		
		if(todo.getTeam().getId() != null) {
			return todoRepository.createByTeam(todo);
		}
		
		return Uni.createFrom().item(() -> null);
	}
}
