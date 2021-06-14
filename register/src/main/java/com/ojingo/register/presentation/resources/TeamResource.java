 package com.ojingo.register.presentation.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAllowedException;
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

import com.ojingo.register.domain.models.Team;
import com.ojingo.register.domain.models.Todo;
import com.ojingo.register.presentation.service.TeamService;
import com.ojingo.register.presentation.service.TodoService;
import com.ojingo.register.presentation.service.UserService;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource {
	
	@Inject 
	TeamService teamService;
	
	@Inject 
	UserService userService;
	
	@Inject 
	TodoService todoService;
	
	@GET
	@Tag(name = "Teams")
	public Response getAll() {		
		return Response.ok(teamService.listAll()).build();
	}
	
	@GET
	@Path("{idTeam}")
	@Tag(name = "Teams")
	public Response getATeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(teamService.findById(idTeam)).build();
	}
	
	@POST
	@Transactional
	@Tag(name = "Teams")
	public Response create(Team dto){
		try {	
			if(dto != null) {
				return Response.ok(teamService.create(dto)).build();
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
	@Tag(name = "Teams")
	public Response update(@PathParam("idTeam") Long idTeam, Team dto) {
		if(idTeam != null && dto != null) {
			Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);
	
			if (teamOptional.isEmpty()) {
				throw new NotFoundException();
			}
			
			if (teamService.update(idTeam, dto).isPersistent()) {			
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
	@Tag(name = "Teams")
	public Response delete(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);
		
		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}

		teamService.delete(idTeam);
		return Response.ok(204).build();	
	}
	
	
	@GET
	@Path("{idTeam}/users")
	@Tag(name = "Users")
	@Tag(name = "Teams")
	public Response getUsersFromTeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userService.listByTeam(teamOptional.get())).build();
	}
	
	@GET
	@Path("{idTeam}/users/{idUser}")
	@Tag(name = "Users")
	@Tag(name = "Teams")
	public Response getUserFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idUser") Long idUser) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userService.findByTeamAndId(teamOptional.get(), idUser)).build();
	}
	
	@GET
	@Path("{idTeam}/todos")
	@Tag(name = "Todos")
	@Tag(name = "Teams")
	public Response getTodosFromTeam(@PathParam("idTeam") Long idTeam) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoService.listByTeam(teamOptional.get())).build();
	}
	
	@GET
	@Path("{idTeam}/todos/{idTodo}")
	@Tag(name = "Todos")
	@Tag(name = "Teams")
	public Response getTodoFromTeam(@PathParam("idTeam") Long idTeam, @PathParam("idTodo") Long idTodo) {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);
		
		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(todoService.findByTeamAndId(teamOptional.get(), idTodo)).build();
	}

	@POST
	@Path("{idTeam}/todos")
	@Transactional
	@Tag(name = "Todos")
	@Tag(name = "Teams")
	public Response createTodoFromTeam(@PathParam("idTeam") Long idTeam, Todo todoDTO) throws Exception {
		Optional<Team> teamOptional = teamService.findByIdOptional(idTeam);

		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		Todo todo = todoService.createFromTeam(teamOptional.get(), todoDTO);
		
		if (todo.isPersistent()) {			
			return Response.ok(todo).build();			
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
