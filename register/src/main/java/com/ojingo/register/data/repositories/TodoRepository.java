package com.ojingo.register.data.repositories;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.entities.Todo;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ApplicationScoped
public class TodoRepository implements PanacheRepository<Todo> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TodoRepository.class);
	
	public Todo create(Todo todo) {
		LOGGER.info("register -> creating a new todo: {0}", todo.description);
		
		this.persist(todo);
		
		return todo;
	}
	
	public Todo update(Todo todoToUpdate, Todo todo) {
		LOGGER.info("register -> updating a todo: {0}", todo.description);
		
		if(todo.description != null) {
			todoToUpdate.description = todo.description;			
		}
		
		if(todo.team != null) {						
			todoToUpdate.team = todo.team;
		}
				
		this.persist(todoToUpdate);
		
		return todoToUpdate;
	}
}
