package com.ojingo.todo.domain.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ojingo.todo.domain.entities.Todo;

@Mapper(componentModel = "cdi")
public interface TodoMapper {
	Todo convertToTodo(TodoDTO todoDTO);
	
	@Mapping(target = "user.id", source = "userId")
	@Mapping(target = "team.id", source = "teamId")
	Todo convertToTodo(CreateTodoDTO createTodoDTO);
	
	Todo convertToTodo(UpdateTodoDTO updateTodoDTO);

	TodoDTO convertToTodoDTO(Todo todo);
}
