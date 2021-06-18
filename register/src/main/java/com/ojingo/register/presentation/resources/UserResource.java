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

import com.ojingo.register.data.dto.CreateUserDTO;
import com.ojingo.register.data.dto.UpdateUserDTO;
import com.ojingo.register.data.dto.UserDTO;
import com.ojingo.register.data.dto.UserMapper;
import com.ojingo.register.domain.models.User;
import com.ojingo.register.presentation.service.UserService;

@Path("/users")
@Tag(name = "Users")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
		
	@Inject 
	UserService userService;
	
	@Inject 
	UserMapper userMapper;

	@GET
	public Response getAll() {		
		List<UserDTO> results = new ArrayList<>();
		
		userService.listAll().forEach(u -> results.add(userMapper.convertToUserDTO(u)));		
		
		return Response.ok(results).build();
	}
		
	@GET
	@Path("{idUser}")
	public Response getUser(@PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userService.findByIdOptional(idUser);

		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userMapper.convertToUserDTO(userOptional.get())).build();		
	}
	
	@POST	
	@Transactional
	public Response create(@Valid CreateUserDTO createUserDTO) throws Exception {	
		try {	
			if(createUserDTO != null) {
				return Response.ok(userMapper.convertToUserDTO(
						userService.create(userMapper.convertToUser(createUserDTO)))
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
	@Path("{idUser}")
	@Transactional
	public Response update(@PathParam("idUser") Long idUser, UpdateUserDTO updateUserDTO) {				
		if(idUser != null && updateUserDTO != null) {
			Optional<User> userOptional = userService.findByIdOptional(idUser);
			
			if (userOptional.isEmpty()) {
				throw new NotFoundException();
			}
			
			if (userService.update(userOptional.get(), userMapper.convertToUser(updateUserDTO)).isPersistent()) {			
				return Response.ok("Team has been successfully changed").build();
			} else {
				return Response.status(409).build();
			}
		} else {
			return Response.status(400).build();
		}
	}

	@DELETE
	@Path("{idUser}")
	@Transactional
	public Response delete(@PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userService.findByIdOptional(idUser);
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}

		userService.delete(userOptional.get());
		return Response.ok(204).build();	
	}
}
