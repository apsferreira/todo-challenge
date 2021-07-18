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
import com.ojingo.todo.data.repositories.UserRepository;
import com.ojingo.todo.domain.dto.NoteDTO;
import com.ojingo.todo.domain.dto.NoteMapper;
import com.ojingo.todo.domain.dto.TodoDTO;
import com.ojingo.todo.domain.dto.UserDTO;
import com.ojingo.todo.domain.dto.UserMapper;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/users")
@Tag(name = "Users")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(securitySchemeName = "todo-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/todo/protocol/openid-connect/token")))
public class UserResource {
	
	@Inject
	private UserMapper userMapper;
		
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private NoteMapper noteMapper;
		
	@Inject
	private NoteRepository noteRepository;
	
	
	@GET
	@APIResponse(responseCode = "200", description = "Users returned successfully", 
		content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = TodoDTO.class)))
	@RolesAllowed("ROLE_ADMIN")
	public Multi<UserDTO> getAll() {
		return userRepository.listAll().map(user -> userMapper.convertToUserDTO(user));
	}
	
	@GET
	@Path("{idUser}")
	@APIResponse(responseCode = "200", description = "User returned successfully")		
	@APIResponse(responseCode = "404", description = "User not found")
	@RolesAllowed({"ROLE_USER"})
	@CacheResult(cacheName = "getUser-todo")
	public Uni<Response> getUser(@CacheKey @PathParam("idUser") UUID idUser) {				
		return userRepository.findById(idUser)
				.map(user -> user != null ? Response.ok(user) : Response.status(Status.NOT_FOUND))
				.onItem().transform(ResponseBuilder::build);
	}

	@GET
	@Path("{idUser}/notes")
	@APIResponse(responseCode = "200", description = "Notes returned successfully")		
	@APIResponse(responseCode = "404", description = "User not found")
	@Tag(name = "Users")
	@Tag(name = "Notes")
	@RolesAllowed({"ROLE_USER"})
	public Multi<NoteDTO> getNotesByUser(@CacheKey @PathParam("idUser") UUID idUser, @QueryParam List<String> filter, @QueryParam String sort) {				
		return noteRepository.listByUser(idUser, filter, sort)
				.map(note -> noteMapper.convertToNoteDTO(note));
	}
}
