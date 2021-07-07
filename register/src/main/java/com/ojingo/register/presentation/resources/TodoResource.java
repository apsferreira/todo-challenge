 package com.ojingo.register.presentation.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
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

import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.ojingo.register.data.repositories.TodoRepository;
import com.ojingo.register.domain.dto.CreateTodoDTO;
import com.ojingo.register.domain.dto.TodoDTO;
import com.ojingo.register.domain.dto.TodoMapper;
import com.ojingo.register.domain.dto.UpdateTodoDTO;
import com.ojingo.register.domain.entities.Todo;

@Path("/todos")
@Tag(name = "Todos")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(securitySchemeName = "todo-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/todo/protocol/openid-connect/token")))
public class TodoResource {
	
	@Inject
	TodoMapper todoMapper;
	
	@Inject
	TodoRepository todoRepository;
	
	@GET
	@APIResponse(responseCode = "200", description = "Todo list returned successfully")
	@RolesAllowed("ROLE_ADMIN")
	public Response getAll() {		
		List<TodoDTO> results = new ArrayList<>();
		
		todoRepository.listAll().forEach(t -> results.add(todoMapper.convertToTodoDTO(t)));
		
		return Response.ok(results).build();
	}
		
	@GET
	@Path("{idTodo}")
	@APIResponse(responseCode = "200", description = "Todo returned successfully")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
	public Response getTodo(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoMapper.convertToTodoDTO(todoOptional.get())).build();
	}
	
	@POST	
	@APIResponse(responseCode = "201", description = "New Todo created successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")	
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response create(@Valid CreateTodoDTO createTodoDTO) throws Exception {
		Todo todo = todoMapper.convertToTodo(createTodoDTO);
		
		if (todoRepository.create(todo).isPersistent()) {					
			return Response.created(URI.create("/todos/" + todo.id.toString())).build();
		} else {
			return Response.status(422).build();
		}
	}
	
	@PUT
	@Path("{idTodo}")
	@APIResponse(responseCode = "200", description = "Todo updated successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@APIResponse(responseCode = "409", description = "conflict with todos")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response update(@PathParam("idTodo") Long idTodo, UpdateTodoDTO updateTodoDTO) {
		if(idTodo != null && updateTodoDTO != null) {
			Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);
			
			if (todoOptional.isEmpty()) {
				throw new NotFoundException();
			}
			
			if (todoRepository.update(todoOptional.get(), todoMapper.convertToTodo(updateTodoDTO)).isPersistent()) {			
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
	@APIResponse(responseCode = "204", description = "Todo deleted successfully")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response delete(@PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);
		
		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		todoRepository.delete(todoOptional.get());
		return Response.ok(204).build();	
	}
	
}
