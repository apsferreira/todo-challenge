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

	public Todo findByTeamAndId(Team team, Long id) {
		return todoRepository.find("team = ?1 and id = ?2", team, id).firstResult();
	}	
	
	public Todo findByUser(User dto) {		
		return todoRepository.find("user", dto).firstResult();
	}
	
	public Todo create(Todo todoDTO) throws Exception {		
		Todo todo = new Todo();
		
		todo.description = todoDTO.description;
		todo.user = todoDTO.user;
		todo.team = todoDTO.team;;

		todoRepository.persist(todo);	
		
		return todo;
	}
	
	public Todo createFromTeam(Team teamDTO, Todo todoDTO) throws Exception {
		Todo todo = new Todo();
		
		todo.description = todoDTO.description;
		todo.team = teamDTO;
		
		todoRepository.persist(todo);	
		
		return todo;
	}
	
	public Todo createFromUser(User userDTO) throws Exception { 
		if(this.findByUser(userDTO) != null) {
			throw new Exception("Todo already exists");
		}
		
		Todo todo = new Todo();
		
		todo.description = "List of " + userDTO.name;
		todo.user = userDTO;
		
		todoRepository.persist(todo);	
		
		return todo;
	}
	
	public Todo update(Long id, Todo dto) {	
		Optional<Todo> todoOptional = this.findByIdOptional(id);
		
		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		Todo todo = todoOptional.get();
		
		todo.description = dto.description; 
		
		todo.persist();
		
		return todo;
	}
	
	public void delete(Long id) {
		Optional<Todo> todoOptional = this.findByIdOptional(id);
		
		todoOptional.ifPresentOrElse(Todo::delete, () -> {
			throw new NotFoundException();
		});
	}

}
