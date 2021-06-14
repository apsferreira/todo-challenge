 package com.ojingo.register.presentation.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.ojingo.register.domain.models.Todo;
import com.ojingo.register.domain.models.User;
import com.ojingo.register.presentation.service.TodoService;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {
	
	@Inject 
	TodoService todoService;

	@GET
	@Tag(name = "Todos")	
	public Response getTodos() {		
		return Response.ok(todoService.listAll()).build();
	}
		
	@GET
	@Path("{idTodo}")
	@Tag(name = "Todos")	
	public Response getUser(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoService.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoOptional.get()).build();
		
	}
	
	@POST	
	@Transactional
	@Tag(name = "Todos")
	public Response create(Todo dto) throws Exception {
		Todo todo = todoService.create(dto);
		
		if (todo.isPersistent()) {			
			return Response.ok(todo).build();			
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@PUT
	@Path("{idTodo}")
	@Transactional
	@Tag(name = "Todos")
	public Response updateUser(@PathParam("idTodo") Long idTodo, Todo dto) {
		if (todoService.update(idTodo, dto).isPersistent()) {			
			return Response.ok("Todo has been successfully changed").build();
		} else {
			return Response.notModified("Error when changing the Todo: " + idTodo).build();
		}
	}

	@DELETE
	@Path("{idTodo}")
	@Transactional
	@Tag(name = "Todos")
	public Response deleteUser(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> userOptional = todoService.findByIdOptional(idTodo);
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}

		todoService.delete(idTodo);
		return Response.ok("Todo was deleted sucessfully").build();		
	}
	
}
