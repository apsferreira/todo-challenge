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

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.ojingo.register.data.repositories.UserRepository;
import com.ojingo.register.domain.dto.UserDTO;
import com.ojingo.register.domain.dto.UserMapper;
import com.ojingo.register.domain.entities.User;

import io.quarkus.cache.CacheResult;

@Path("/users")
@Tag(name = "Users")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
		
	@Inject 
	UserMapper userMapper;

	@Inject 
	UserRepository userRepository;

	@GET
	@APIResponse(responseCode = "200", description = "User list returned successfully")
	@RolesAllowed("ROLE_ADMIN")
	public Response getAll() {		
		List<UserDTO> results = new ArrayList<>();
		
		userRepository.listAll().forEach(u -> results.add(userMapper.convertToUserDTO(u)));		
		
		return Response.ok(results).build();
	}
		
	@GET
	@Path("{idUser}")
	@APIResponse(responseCode = "200", description = "User returned successfully")
	@APIResponse(responseCode = "404", description = "User not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getUser-register")
	public Response getUser(@PathParam("idUser") Long idUser) {
		Optional<User> userOptional = userRepository.findByIdOptional(idUser);

		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		return Response.ok(userMapper.convertToUserDTO(userOptional.get())).build();		
	}	
}
