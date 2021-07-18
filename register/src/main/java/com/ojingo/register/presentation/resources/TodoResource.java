 package com.ojingo.register.presentation.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import com.ojingo.register.domain.dto.TodoDTO;
import com.ojingo.register.domain.dto.TodoMapper;
import com.ojingo.register.domain.entities.Todo;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;

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
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getTodo-register")
	public Response getTodo(@CacheKey @PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoMapper.convertToTodoDTO(todoOptional.get())).build();
	}
}
