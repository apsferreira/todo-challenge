 package com.ojingo.register.presentation.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.ojingo.register.data.dto.CreateTodoDTO;
import com.ojingo.register.data.dto.TodoDTO;
import com.ojingo.register.data.dto.TodoMapper;
import com.ojingo.register.data.dto.UpdateTodoDTO;
import com.ojingo.register.domain.models.Todo;
import com.ojingo.register.presentation.service.TodoService;

@Path("/todos")
@Tag(name = "Todos")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {
	
	@Inject
	TodoMapper todoMapper;
	
	@Inject
	TodoService todoService;
	
	@GET
	public Response getAll() {		
		List<TodoDTO> results = new ArrayList<>();
		
		todoService.listAll().forEach(t -> results.add(todoMapper.convertToTodoDTO(t)));
		
		return Response.ok(results).build();
	}
		
	@GET
	@Path("{idTodo}")
	public Response getTodo(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoService.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoMapper.convertToTodoDTO(todoOptional.get())).build();
	}
	
	@POST	
	@Transactional
	public Response create(@Valid CreateTodoDTO createTodoDTO) throws Exception {
		try {	
			if(createTodoDTO != null) {
				return Response.ok(todoMapper.convertToTodoDTO(
						todoService.create(todoMapper.convertToTodo(createTodoDTO)))
				).build();
			} else {
				return Response.status(400).build();
			}
			
		} catch (IllegalArgumentException e) {
			return Response.status(422).build();
		} catch (Exception e) {
			return Response.status(409).build();
		}
	}
	
	@PUT
	@Path("{idTodo}")
	@Transactional
	public Response update(@PathParam("idTodo") Long idTodo, UpdateTodoDTO updateTodoDTO) {
		if(idTodo != null && updateTodoDTO != null) {
			Optional<Todo> todoOptional = todoService.findByIdOptional(idTodo);
			
			if (todoOptional.isEmpty()) {
				throw new NotFoundException();
			}
			
			if (todoService.update(todoOptional.get(), todoMapper.convertToTodo(updateTodoDTO)).isPersistent()) {			
				return Response.ok("Team has been successfully changed").build();
			} else {
				return Response.status(409).build();
			}
		} else {
			return Response.status(400).build();
		}
	}

	@DELETE
	@Path("{idTodo}")
	@Transactional
	public Response delete(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoService.findByIdOptional(idTodo);
		
		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		todoService.delete(todoOptional.get());
		return Response.ok(204).build();	
	}
	
}
