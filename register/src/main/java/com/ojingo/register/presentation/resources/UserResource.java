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

import com.ojingo.register.domain.models.User;
import com.ojingo.register.presentation.service.TodoService;
import com.ojingo.register.presentation.service.UserService;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
		
	@Inject 
	UserService userService;
	
	@Inject 
	TodoService todoService;

	@GET
	@Tag(name = "Users")	
	public Response getAll() {		
		return Response.ok(userService.listAll()).build();
	}
		
	@GET
	@Path("{idUser}")
	@Tag(name = "Users")	
	public Response getAUser(@PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userService.findByIdOptional(idUser);

		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userOptional.get()).build();		
	}
	
	@POST	
	@Transactional
	@Tag(name = "Users")
	public Response create(@PathParam("idTeam") Long idTeam, User dto) throws Exception {	
		User user = userService.create(dto);
		
		if (user.isPersistent() && todoService.createFromUser(user).isPersistent()) {			
			return Response.ok(user).build();			
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PUT
	@Path("{idUser}")
	@Transactional
	@Tag(name = "Users")
	public Response update(@PathParam("idUser") Long idUser, User dto) {		
		Optional<User> userOptional = userService.findByIdOptional(idUser);
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		if (userService.update(idUser, dto).isPersistent()) {			
			return Response.ok("User has been successfully changed").build();
		} else {
			return Response.notModified("Error when changing the user: " + idUser).build();
		}
	}

	@DELETE
	@Path("{idUser}")
	@Transactional
	@Tag(name = "Users")
	public Response delete(@PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userService.findByIdOptional(idUser);
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}

		userService.delete(idUser);
		return Response.ok("User was deleted sucessfully").build();		
	}
}
