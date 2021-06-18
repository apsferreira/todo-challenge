package com.ojingo.register.data.dto;

import org.mapstruct.Mapper;

import com.ojingo.register.domain.models.Todo;

@Mapper(componentModel = "cdi")
public interface TodoMapper {
	Todo convertToTodo(TodoDTO todoDTO);
	
	Todo convertToTodo(CreateTodoDTO createTodoDTO);
	
	Todo convertToTodo(UpdateTodoDTO updateTodoDTO);

	Todo convertToTodo(CreateTodoTeamDTO createTodoDTO);
	
	Todo convertToTodo(UpdateTodoTeamDTO updateTodoTeamDTO);

	TodoDTO convertToTodoDTO(Todo todo);
}
