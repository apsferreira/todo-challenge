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
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.ojingo.register.data.dto.CreateTeamDTO;
import com.ojingo.register.data.dto.CreateTodoTeamDTO;
import com.ojingo.register.data.dto.TeamDTO;
import com.ojingo.register.data.dto.TeamMapper;
import com.ojingo.register.data.dto.TodoDTO;
import com.ojingo.register.data.dto.TodoMapper;
import com.ojingo.register.data.dto.UpdateTeamDTO;
import com.ojingo.register.data.dto.UpdateTodoTeamDTO;
import com.ojingo.register.data.dto.UserDTO;
import com.ojingo.register.data.dto.UserMapper;
import com.ojingo.register.domain.models.Team;
import com.ojingo.register.domain.models.Todo;
import com.ojingo.register.presentation.service.TeamService;
import com.ojingo.register.presentation.service.TodoService;
import com.ojingo.register.presentation.service.UserService;

@Path("/teams")
@Tag(name = "Teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource {
	
	@Inject
	TeamMapper teamMapper;
	
	@Inject
	UserMapper userMapper;
	
	@Inject
	TodoMapper todoMapper;
		
	@Inject 
	TeamService teamService;
	
	@Inject 
	UserService userService;
	
	@Inject 
	TodoService todoService;
	
	@GET
	public Response getAll() {		
		List<TeamDTO> results = new ArrayList<>();
		
		teamService.listAll().forEach(t -> results.add(teamMapper.convertToTeamDTO(t)));
		
		return Response.ok(results).build();
	}
	
	@GET
	@Path("{idTeam}")
	public Response getTeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}		
		
		return Response.ok(teamMapper.convertToTeamDTO(teamOptional.get())).build();
	}
	
	@POST
	@Transactional
	public Response create(@Valid CreateTeamDTO createTeamDTO){
		try {	
			if(createTeamDTO != null) {
				return Response.ok(teamMapper.convertToTeamDTO(
						teamService.create(teamMapper.convertToTeam(createTeamDTO))
					)).build();
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
	@Path("{idTeam}")
	@Transactional
	public Response update(@PathParam("idTeam") Long idTeam, UpdateTeamDTO updateTeamDTO) {
		if(idTeam != null && updateTeamDTO != null) {
			Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);
	
			if (teamOptional.isEmpty()) {
				throw new NotFoundException();
			}
			
			if (teamService.update(teamOptional.get(), teamMapper.convertToTeam(updateTeamDTO)).isPersistent()) {			
				return Response.ok("Team has been successfully changed").build();
			} else {
				return Response.status(409).build();
			}
		} else {
			return Response.status(400).build();
		}
	}
	
	@DELETE
	@Path("{idTeam}")
	@Transactional
	public Response delete(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);
		
		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		teamService.delete(teamOptional.get());
		return Response.ok(204).build();	
	}
	
	@GET
	@Path("{idTeam}/users")
	@Tag(name = "Teams")
	@Tag(name = "Users")
	public Response getUsersFromTeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		List<UserDTO> results = new ArrayList<>();
		
		userService.listByTeam(teamOptional.get()).forEach(t -> results.add(userMapper.convertToUserDTO(t)));
		
		return Response.ok(results).build();
	}
	
	@GET
	@Path("{idTeam}/users/{idUser}")
	@Tag(name = "Todos")
	public Response getUserFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idUser") Long idUser) {
		Optional<UserDTO> userOptional = Optional.ofNullable(userMapper.convertToUserDTO(userService.findByIdTeamAndId(idTeam, idUser)));
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userOptional.get()).build();
	}
	
	@GET
	@Path("{idTeam}/todos")
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	public Response getTodosFromTeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		List<TodoDTO> results = new ArrayList<>();
		
		todoService.listByTeam(teamOptional.get()).forEach(t -> results.add(todoMapper.convertToTodoDTO(t)));
		
		
		return Response.ok(results).build();
	}
	
	@GET
	@Path("{idTeam}/todos/{idTodo}")
	@Tag(name = "Todos")
	public Response getTodoFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTodo") Long idTodo) {
		Optional<Todo> todoOptional = Optional.ofNullable(todoService.findByIdTeamAndId(idTeam, idTodo));
		
		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoMapper.convertToTodoDTO(todoOptional.get())).build();
	}

	@POST
	@Path("{idTeam}/todos")
	@Transactional
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	public Response createTodoFromTeam(@PathParam("idTeam") Long idTeam, @Valid CreateTodoTeamDTO createTodoDTO) throws Exception {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		Todo todo = todoService.createFromTeam(teamOptional.get(), todoMapper.convertToTodo(createTodoDTO));
		
		if (todo.isPersistent()) {			
			return Response.ok(todoMapper.convertToTodoDTO(todo)).build();			
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PUT
	@Path("{idTeam}/todos/{idTodo}")
	@Transactional
	@Tag(name = "Teams")
	@Tag(name = "Todos")
	public Response updateTodoFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTodo") Long idTodo, UpdateTodoTeamDTO updateTodoTeamDTO) throws Exception {
		Optional<Todo> todoOptional = Optional.ofNullable(todoService.findByIdTeamAndId(idTeam, idTodo));

		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}		
		
		if (todoService.update(todoOptional.get(), todoMapper.convertToTodo(updateTodoTeamDTO)).isPersistent()) {			
			return Response.ok("Todo has been successfully changed").build();
		} else {
			return Response.status(409).build();
		}
	}
	
	@DELETE
	@Path("{idTeam}/todos/{idTodo}")
	@Transactional
	public Response deleteTodoFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTeam") Long idTodo) {
		Optional<Todo> todoOptional = Optional.ofNullable(todoService.findByIdTeamAndId(idTeam, idTodo));
		
		if (todoOptional.isEmpty()) {
			throw new NotFoundException();
		}

		todoService.delete(todoOptional.get());
		return Response.ok(204).build();	
	}
}
