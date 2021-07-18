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
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

import com.ojingo.register.data.repositories.TeamRepository;
import com.ojingo.register.data.repositories.TodoRepository;
import com.ojingo.register.data.repositories.UserRepository;
import com.ojingo.register.domain.dto.CreateTeamDTO;
import com.ojingo.register.domain.dto.CreateTodoTeamDTO;
import com.ojingo.register.domain.dto.TeamDTO;
import com.ojingo.register.domain.dto.TeamMapper;
import com.ojingo.register.domain.dto.TodoDTO;
import com.ojingo.register.domain.dto.TodoMapper;
import com.ojingo.register.domain.dto.UpdateTeamDTO;
import com.ojingo.register.domain.dto.UpdateTodoTeamDTO;
import com.ojingo.register.domain.dto.UserDTO;
import com.ojingo.register.domain.dto.UserMapper;
import com.ojingo.register.domain.entities.Team;
import com.ojingo.register.domain.entities.Todo;
import com.ojingo.register.domain.entities.User;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teams")
@SecurityScheme(securitySchemeName = "todo-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/todo/protocol/openid-connect/token")))
public class TeamResource {

	@Inject
	TeamMapper teamMapper;

	@Inject
	UserMapper userMapper;

	@Inject
	TodoMapper todoMapper;
	
	@Inject 
	TeamRepository teamRepository;

	@Inject 
	UserRepository userRepository;
	
	@Inject
	TodoRepository todoRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamResource.class);


	@GET
	@APIResponse(responseCode = "200", description = "Team list returned successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@RolesAllowed("ROLE_ADMIN")	
	public Response getAll() {
		List<TeamDTO> results = new ArrayList<>();
		
		LOGGER.info("register -> TeamResource -> getall route was requested");

		teamRepository.listAll().forEach(t -> results.add(teamMapper.convertToTeamDTO(t)));

		return Response.ok(results).build();
	}

	@GET
	@Path("{idTeam}")
	@APIResponse(responseCode = "200", description = "Team returned successfully", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = TeamDTO.class)))
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getTeam-register")
	public Response getTeam(@CacheKey @PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);
		
		LOGGER.info("register -> TeamResource -> getTeam route was requested");

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		return Response.ok(teamMapper.convertToTeamDTO(teamOptional.get())).build();
	}

	@GET
	@Path("{idTeam}/users")
	@Tag(name = "Teams")
	@Tag(name = "Users")
	@APIResponse(responseCode = "200", description = "List of users by team returned successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@RolesAllowed("ROLE_ADMIN")
	public Response getUsersByTeam(@CacheKey @PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);
		
		LOGGER.info("register -> TeamResource -> getUsersByTeam route was requested");

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		List<UserDTO> results = new ArrayList<>();

		userRepository.list("team", teamOptional.get()).forEach(t -> results.add(userMapper.convertToUserDTO(t)));

		return Response.ok(results).build();
	}
	
	@GET
	@Path("{idTeam}/todos")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	@APIResponse(responseCode = "200", description = "List of Todo by team returned successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@RolesAllowed("ROLE_USER")
	public Response getTodosByTeam(@CacheKey @PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);
		
		LOGGER.info("register -> TeamResource -> getTodosByTeam route was requested");

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		List<TodoDTO> results = new ArrayList<>();

		todoRepository.list("team", teamOptional.get()).forEach(todo -> results.add(todoMapper.convertToTodoDTO(todo)));

		return Response.ok(results).build();
	}

	@GET
	@Path("{idTeam}/todos/{idTodo}")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	@APIResponse(responseCode = "200", description = "Todo by team returned successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getTodoByTeam-register")
	public Response getTodoByTeam(@CacheKey @PathParam("idTeam") Long idTeam, @CacheKey @PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		if(todoOptional.get().team.id.equals(idTeam)) {
			return Response.ok(todoMapper.convertToTodoDTO(todoOptional.get())).build();			
		} else {
			return Response.status(404).build();
		}
	}
	
	@GET
	@Path("{idTeam}/users/{idUser}")
	@Tag(name = "Teams")
	@Tag(name = "Users")
	@APIResponse(responseCode = "200", description = "Users by team returned successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "User not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getUserByTeam-register")
	public Response getUserByTeam(@CacheKey @PathParam("idTeam") Long idTeam, @CacheKey @PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userRepository.findByIdOptional(idUser);

		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		if(userOptional.get().team.id.equals(idTeam)) {
			return Response.ok(userMapper.convertToUserDTO(userOptional.get())).build();			
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@APIResponse(responseCode = "201", description = "New team created successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "422", description = "Team already exists")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response create(@Valid CreateTeamDTO createTeamDTO) {
		Team team = teamMapper.convertToTeam(createTeamDTO);

		if (teamRepository.create(team).isPersistent()) {
			return Response.created(URI.create("/teams/" + team.id.toString())).build();
		} else {
			return Response.status(422).build();
		}
	}

	@POST
	@Path("{idTeam}/todos")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	@APIResponse(responseCode = "201", description = "Todo by team created successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@APIResponse(responseCode = "500", description = "Error to persist Todo")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response createTodoByTeam(@PathParam("idTeam") Long idTeam, @Valid CreateTodoTeamDTO createTodoDTO)
			throws Exception {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		Todo todo = todoMapper.convertToTodo(createTodoDTO);
		todo.team = teamOptional.get();
		
		if (todoRepository.create(todo).isPersistent()) {
			return Response.created(URI.create("/teams/" + idTeam.toString() + "/todos/" + todo.id.toString())).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PUT
	@Path("{idTeam}")
	@APIResponse(responseCode = "200", description = "Team updated successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@APIResponse(responseCode = "409", description = "conflict with teams")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response update(@PathParam("idTeam") Long idTeam, UpdateTeamDTO updateTeamDTO) {
		if (idTeam != null && updateTeamDTO != null) {
			Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);

			if (teamOptional.isEmpty()) {
				throw new NotFoundException();
			}

			if (teamRepository.update(teamOptional.get(), teamMapper.convertToTeam(updateTeamDTO)).isPersistent()) {
				return Response.ok("Team has been successfully changed").build();
			} else {
				return Response.status(409).build();
			}
		} else {
			return Response.status(400).build();
		}
	}

	@PUT
	@Path("{idTeam}/todos/{idTodo}")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	@APIResponse(responseCode = "200", description = "Todo by team updated successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@APIResponse(responseCode = "409", description = "Conflict with Todos")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response updateTodoByTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTodo") Long idTodo,
			UpdateTodoTeamDTO updateTodoTeamDTO) throws Exception {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);

		if (todoOptional.isEmpty() || !todoOptional.get().team.id.equals(idTeam)) {
			throw new NotFoundException();
		}
		
		if (todoRepository.update(todoOptional.get(), todoMapper.convertToTodo(updateTodoTeamDTO)).isPersistent()) {
			return Response.ok("Todo has been successfully changed").build();
		} else {
			return Response.status(409).build();
		}
	}
	
	@PATCH
	@Path("{idTeam}/users/{idUser}")
	@Tag(name = "Teams")
	@Tag(name = "Users")
	@APIResponse(responseCode = "200", description = "User associate a team with successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@APIResponse(responseCode = "409", description = "Conflict with ussers")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response updateUserByTeam(@PathParam("idTeam") Long idTeam, @PathParam("idUser") Long idUser) throws Exception {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		Optional<User> userOptional = userRepository.findByIdOptional(idUser);

		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		User user = userOptional.get(); 
		user.team = teamOptional.get();
		
		if (userRepository.update(userOptional.get(), user).isPersistent()) {
			return Response.ok("User has been successfully changed").build();
		} else {
			return Response.status(409).build();
		}
	}

	@DELETE
	@Path("{idTeam}")
	@APIResponse(responseCode = "204", description = "Team deleted successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Team not found")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response delete(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamRepository.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		teamRepository.delete(teamOptional.get());
		
		return Response.ok(204).build();
	}

	@DELETE
	@Path("{idTeam}/todos/{idTodo}")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	@APIResponse(responseCode = "204", description = "Todo by team deleted successfully")
	@APIResponse(responseCode = "401", description = "Not authorized")
	@APIResponse(responseCode = "403", description = "Forbidden")
	@APIResponse(responseCode = "404", description = "Todo not found")
	@RolesAllowed("ROLE_ADMIN")
	@Transactional
	public Response deleteTodoByTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = todoRepository.findByIdOptional(idTodo);

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}

		todoRepository.delete(todoOptional.get());
		
		return Response.ok(204).build();
	}
}
