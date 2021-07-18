 package com.ojingo.todo.presentation.resources;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import com.ojingo.todo.data.repositories.NoteRepository;
import com.ojingo.todo.data.repositories.VersionRepository;
import com.ojingo.todo.domain.dto.CreateNoteDTO;
import com.ojingo.todo.domain.dto.NoteDTO;
import com.ojingo.todo.domain.dto.NoteMapper;
import com.ojingo.todo.domain.dto.UpdateNoteDTO;
import com.ojingo.todo.presentation.services.NoteService;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/notes")
@Tag(name = "Notes")	
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(securitySchemeName = "todo-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/todo/protocol/openid-connect/token")))
public class NoteResource {
	
	@Inject
	NoteMapper noteMapper;
	
	@Inject
	NoteService noteService;
	
	@Inject
	NoteRepository noteRepository;
	
	@Inject
	private VersionRepository versionRepository;
	
	@Inject
	JsonWebToken jwt;

	@GET
	@APIResponse(responseCode = "200", description = "Note list returned successfully")
	@RolesAllowed("ROLE_ADMIN")
	public Multi<NoteDTO> getAll(@QueryParam List<String> filter, @QueryParam String sort) {		
		return noteRepository.listAll(filter, sort).map(note -> noteMapper.convertToNoteDTO(note));
 	}
	
	@GET
	@Path("{idNote}")
	@APIResponse(responseCode = "200", description = "Note returned successfully")
	@APIResponse(responseCode = "404", description = "Note not found")
	@RolesAllowed("ROLE_USER")
	@CacheResult(cacheName = "getNote-todo")
	public Uni<Response> getNote(@CacheKey @PathParam("idNote") UUID idNote) {				
		return noteRepository.findById(idNote)
				.map(note -> note != null ? Response.ok(noteMapper.convertToNoteDTO(note)) : Response.status(Status.NOT_FOUND))
				.onItem().transform(ResponseBuilder::build);
	}
	
	@GET
	@Path("{idNote}/versions")
	@APIResponse(responseCode = "200", description = "Note returned successfully")
	@APIResponse(responseCode = "404", description = "Note not found")
	@RolesAllowed("ROLE_USER")	
	public Multi<Response> getVersionList(@CacheKey @PathParam("idNote") UUID idNote) {				
		return versionRepository.listVersionsOfNote(idNote)
				.onItem().transform(version -> noteRepository.findById(version.getNewNote().getId()).await().indefinitely())
				.map(note -> note != null ? Response.ok(noteMapper.convertToNoteDTO(note)) : Response.status(Status.NOT_FOUND))
				.onItem().transform(ResponseBuilder::build);
	}
	
	@POST	
	@APIResponse(responseCode = "201", description = "New Note created successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@RolesAllowed("ROLE_USER")
	@Transactional
	public Uni<Response> create(@Valid CreateNoteDTO createNoteDTO) {
		return noteRepository.create(noteMapper.convertToNote(createNoteDTO), jwt.getSubject())
				.map(newId -> newId != null ? Response.created(URI.create("/notes/" + newId)) : Response.status(400))
				.onItem().transform(ResponseBuilder::build);
	}
		
	@PUT
	@Path("{idNote}")
	@APIResponse(responseCode = "200", description = "Note updated successfully")
	@APIResponse(responseCode = "403", description = "Forbiden")
	@APIResponse(responseCode = "404", description = "Note not found")
	@APIResponse(responseCode = "409", description = "conflict with Note")
	@RolesAllowed("ROLE_USER")
	@Transactional
	public Uni<Response> update(@PathParam("idNote") UUID idNote, UpdateNoteDTO updateNoteDTO) {
		if(!noteService.validateUser(jwt.getSubject(), idNote)) {
			return Uni.createFrom().item(() -> Response.status(403))
					.onItem().transform(ResponseBuilder::build);
		}
		
		return noteService.update(idNote, noteMapper.convertToNote(updateNoteDTO), jwt.getSubject())
				.map(response -> response != null ? Response.ok("Note has been successfully changed") : Response.status(Status.NOT_FOUND))
				.onItem().transform(ResponseBuilder::build);
	}
	
	@DELETE
	@Path("{idNote}")
	@APIResponse(responseCode = "204", description = "Note deleted successfully")
	@APIResponse(responseCode = "403", description = "Forbiden")
	@APIResponse(responseCode = "404", description = "Note not found")
	@RolesAllowed("ROLE_USER")
	@Transactional
	public Uni<Response> delete(@PathParam("idNote") UUID idNote) {
		if(!noteService.validateUser(jwt.getSubject(), idNote)) {
			return Uni.createFrom().item(() -> Response.status(403))
					.onItem().transform(ResponseBuilder::build);
		}
		
		return noteRepository.delete(idNote)
				.map(response -> response ? Response.ok("Note deleted successfully") : Response.status(400))
				.onItem().transform(ResponseBuilder::build);
	}	
	
	@PATCH
	@Path("{idNote}/favorite")
	@APIResponse(responseCode = "200", description = "Note updated successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "403", description = "Forbiden")
	@APIResponse(responseCode = "404", description = "Note not found")
	@APIResponse(responseCode = "409", description = "conflict with Note")
	@RolesAllowed("ROLE_USER")
	@Transactional
	public Uni<Response> favoriteNote(@PathParam("idNote") UUID idNote) {
		if(!noteService.validateUser(jwt.getSubject(), idNote)) {
			return Uni.createFrom().item(() -> Response.status(403))
					.onItem().transform(ResponseBuilder::build);
		}
		
		return noteService.favoriteNote(idNote)
				.map(response -> response ? Response.ok("Note has been favorited/unfavorited sucessfully") : Response.status(400))
				.onItem().transform(ResponseBuilder::build);
	}
	
	@PATCH
	@Path("{idNote}/done")
	@APIResponse(responseCode = "200", description = "Note updated successfully")
	@APIResponse(responseCode = "400", description = "Invalid data")
	@APIResponse(responseCode = "403", description = "Forbiden")
	@APIResponse(responseCode = "404", description = "Note not found")
	@APIResponse(responseCode = "409", description = "conflict with Note")
	@RolesAllowed("ROLE_USER")
	@Transactional
	public Uni<Response> doneNote(@PathParam("idNote") UUID idNote) {
		if(!noteService.validateUser(jwt.getSubject(), idNote)) {
			return Uni.createFrom().item(() -> Response.status(403))
					.onItem().transform(ResponseBuilder::build);
		}
		
		return noteService.doneNote(idNote)
				.map(response -> response ? Response.ok("Note has been done/undone sucessfully") : Response.status(400))
				.onItem().transform(ResponseBuilder::build);
	}
}
