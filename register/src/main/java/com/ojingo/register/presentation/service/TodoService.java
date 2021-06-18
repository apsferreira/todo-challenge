package com.ojingo.register.presentation.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import com.ojingo.register.data.repository.TodoRepository;
import com.ojingo.register.domain.models.Team;
import com.ojingo.register.domain.models.Todo;
import com.ojingo.register.domain.models.User;

@ApplicationScoped
public class TodoService {
	
	@Inject 
	TodoRepository todoRepository;
	
	public List<Todo> listAll() {
		return todoRepository.listAll();
	}
	
	public List<Todo> listByTeam(Team team) {
		return todoRepository.list("team", team);
	}

	public Todo findById(Long id) {
		return todoRepository.findById(id);
	}
	
	public Optional<Todo> findByIdOptional(Long id) {
		return todoRepository.findByIdOptional(id);
	}
	
	public Todo findByIdTeamAndId(Long idTeam, Long id) {
		return todoRepository.find("team.id = ?1 and id = ?2", idTeam, id).firstResult();
	}
	
	public Todo findByUser(User dto) {		
		return todoRepository.find("user", dto).firstResult();
	}
	
	public Todo create(Todo todo) throws Exception {		
		
		todoRepository.persist(todo);	
		
		return todo;
	}
	
	public Todo createFromTeam(Team team, Todo todo) throws Exception {
		todo.team = team;
		
		todoRepository.persist(todo);	
		
		return todo;
	}
	
	public Todo createFromUser(User user) throws Exception { 
		if(this.findByUser(user) != null) {
			throw new Exception("Todo already exists");
		}
		
		Todo newTodo = new Todo();
		
		newTodo.description = "To Do List of " + user.name;
		newTodo.user = user;
		
		todoRepository.persist(newTodo);	
		
		return newTodo;
	}
	
	public Todo update(Todo todoToUpdate, Todo todo) {
		todoToUpdate.description = todo.description; 
		
		todoRepository.persist(todoToUpdate);
		
		return todoToUpdate;
	}
	
	public void delete(Todo todo) {
		todoRepository.delete(todo);
	}
}
