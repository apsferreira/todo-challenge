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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoteService.class);
	
	public Uni<Boolean> favoriteNote (UUID id) {
    	LOGGER.info("Favoriting/Unfavoriting note: {0}", id);
    		
    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();	
    	noteOriginal.setFavorite(!noteOriginal.isFavorite());
    	
    	return noteRepository.updateFavorite(id, noteOriginal);
    }
    
    public Uni<Boolean> doneNote (UUID id) {
    	LOGGER.info("Done/Undone note: {0}", id);

    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();	
    	noteOriginal.setDone(!noteOriginal.isDone());

    	return noteRepository.updateDone(id, noteOriginal);
    }    
    
    public Uni<UUID> update (UUID id, Note note, String subjectJWT) {
    	LOGGER.info("Updating note: {0}", id);
    	
    	Note noteOriginal = noteRepository.findById(id).await().indefinitely();		    	
    	noteOriginal.setDescription(note.getDescription());
    	
    	UUID newNoteId = noteRepository.create(noteOriginal, subjectJWT).await().indefinitely();	
	    
    	Version version = versionRepository.findByNewNoteId(id).await().indefinitely();
    	
    	if (version != null) {
    		return versionRepository.create(Version.of(version.getNoteParent(), Note.of(newNoteId)));
    	} else {
    		return versionRepository.create(Version.of(Note.of(id), Note.of(newNoteId)));
    	}
    }

	public boolean validateUser(String subjectJWT, UUID idNote) {
    	Note noteOriginal = noteRepository.findById(idNote).await().indefinitely();		    	

    	if (noteOriginal.getSubjectJWT().equals(subjectJWT) || noteOriginal.getSubjectJWT().equals("initial-persist")) {
    		return true;
    	}
    	
		return false;
	}

	public Uni<Note> listVersionsOfNote(UUID idNote) {
		
    	Version version = versionRepository.findByNewNoteId(idNote).await().indefinitely();

		return noteRepository.findById(version.getNoteParent().getId());
	}
}
