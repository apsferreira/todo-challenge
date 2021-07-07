package com.ojingo.register.data.repositories;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.entities.Todo;

import io.quarkus.hibernate.orm.panache.PanacheRepository;


@ApplicationScoped
public class TodoRepository implements PanacheRepository<Todo> {
	
	public Todo create(Todo todo) {
		this.persist(todo);
		
		return todo;
	}
	
	public Todo update(Todo todoToUpdate, Todo todo) {
		if(todo.description != null) {
			todoToUpdate.description = todo.description;			
		}
		
		if(todo.team != null) {						
			todoToUpdate.team = todo.team;
		}
		
		if(todo.user != null) {						
			todoToUpdate.user = todo.user;
		}
		
		this.persist(todoToUpdate);
		
		return todoToUpdate;
	}
}
