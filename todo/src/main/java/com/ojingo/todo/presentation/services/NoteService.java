package com.ojingo.todo.presentation.services;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.data.repositories.NoteRepository;
import com.ojingo.todo.data.repositories.VersionRepository;
import com.ojingo.todo.domain.entities.Note;
import com.ojingo.todo.domain.entities.Version;

import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@ApplicationScoped
public class NoteService {
	
	@Inject
	private NoteRepository noteRepository;
	
	@Inject
	private VersionRepository versionRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoteRepository.class);
	
	public Uni<Boolean> favoriteNote (UUID id) {
    	LOGGER.info("Favoriting/Unfavoriting note " + id);
    		
    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();	
    	noteOriginal.setFavorite(!noteOriginal.isFavorite());
    	
    	return noteRepository.updateFavorite(id, noteOriginal);
    }
    
    public Uni<Boolean> doneNote (UUID id) {
    	LOGGER.info("Done/Undone note " + id);

    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();	
    	noteOriginal.setDone(!noteOriginal.isDone());

    	return noteRepository.updateDone(id, noteOriginal);
    }    
    
    public Uni<UUID> update (UUID id, Note note, String subjectJWT) {
    	LOGGER.info("Updating note: " + id);
    	
    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();		    	
    	noteOriginal.setDescription(note.getDescription());
    	
    	UUID newNoteId = noteRepository.create(noteOriginal, subjectJWT).await().indefinitely();	
	    							
		return versionRepository.create(Version.of(Note.of(id), Note.of(newNoteId)));
    }

	public boolean validateUser(String subjectJWT, UUID idNote) {
    	Note noteOriginal = noteRepository.findById(idNote).await().indefinitely();		    	

    	if (noteOriginal.getSubjectJWT().equals(subjectJWT)) {
    		return true;
    	}
    	
		return false;
	}
}
