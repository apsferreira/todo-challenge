 package com.ojingo.todo.presentation.resources;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import com.ojingo.todo.data.repositories.NoteRepository;
import com.ojingo.todo.data.repositories.TodoRepository;
import com.ojingo.todo.domain.dto.NoteDTO;
import com.ojingo.todo.domain.dto.NoteMapper;
import com.ojingo.todo.domain.dto.TodoDTO;
import com.ojingo.todo.domain.dto.TodoMapper;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Path("/todos")
@Tag(name = "Todos")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(securitySchemeName = "todo-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/todo/protocol/openid-connect/token")))
public class TodoResource {
	
	@Inject
	private TodoMapper todoMapper;
	
	@Inject
	private NoteMapper noteMapper;
		
	@Inject
	private TodoRepository todoRepository;
	
	@Inject
	private NoteRepository noteRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TodoResource.class);
	
	@GET
	@APIResponse(responseCode = "200", description = "Todo lists returned successfully", 
		content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = TodoDTO.class)))
		@RolesAllowed("ROLE_ADMIN")	
	public Multi<TodoDTO> getAll() {
		LOGGER.info("Todo -> TodoResource -> getAll route was requested");
		
		return todoRepository.listAll().map(todo -> todoMapper.convertToTodoDTO(todo));
	}
	
	@GET
	@Path("{idTodo}")
	@APIResponse(responseCode = "200", description = "Todo returned successfully")		
	@APIResponse(responseCode = "404", description = "Todo not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getNote-todo")
	public Uni<Response> getTodo(@CacheKey @PathParam("idTodo") UUID idTodo) {
		LOGGER.info("Todo -> TodoResource -> getTodo route was requested");
		
		return todoRepository.findById(idTodo)
				.map(todo -> todo != null ? Response.ok(todo) : Response.status(Status.NOT_FOUND))
				.onItem().transform(ResponseBuilder::build);
	}
	
	@GET
	@Path("{idTodo}/notes")
	@APIResponse(responseCode = "200", description = "Todo returned successfully")		
	@APIResponse(responseCode = "404", description = "Todo not found")
	@Tag(name = "Todos")
	@Tag(name = "Notes")
	@RolesAllowed("ROLE_USER")
	public Multi<NoteDTO> getNotesByTodo(@CacheKey @PathParam("idTodo") UUID idTodo, @QueryParam List<String> filter, @QueryParam String sort) {
		LOGGER.info("Todo -> TodoResource -> getNotesByTodo route was requested");
		
		return noteRepository.listByTodo(idTodo, filter, sort)
				.map(note -> noteMapper.convertToNoteDTO(note));
	}	
}
