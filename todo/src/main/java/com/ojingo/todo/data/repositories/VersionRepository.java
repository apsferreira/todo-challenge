package com.ojingo.todo.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.domain.entities.Note;
import com.ojingo.todo.domain.entities.Version;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

@ApplicationScoped
public class VersionRepository {
	
	@Inject
	private PgPool client;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionRepository.class);	
	
    public Uni<UUID> create(Version version) {
    	LOGGER.info("Creating a new version row");
    	
    	return this.client
    		.preparedQuery("INSERT INTO versions (note_parent_id, new_note_id) VALUES ($1, $2) RETURNING id;")
    		.execute(Tuple.of(version.getNoteParent().getId(), version.getNewNote().getId())).onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));    	
    }
    
    public Uni<Boolean> delete (UUID id) {
    	LOGGER.info("Deleting note " + id);

    	return this.client
			.preparedQuery("DELETE FROM notes where id = $1;")
			.execute(Tuple.of(id))
	            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }
    
    public Multi<Version> listVersionsOfNote(UUID idNote) {
    	LOGGER.info("Get List of Versions of note: " + idNote);

        return this.client
        		.preparedQuery("SELECT * FROM versions WHERE note_parent_id = $1").execute(Tuple.of(idNote))
        		.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(version -> this.fromVersion(version));
    }
    
    public Version fromVersion(Row row) {
		Note note_parent_id = Note.of(row.getUUID("note_parent_id"));
		Note new_note_id = Note.of(row.getUUID("new_note_id"));

		return Version.of(row.getUUID("id"), note_parent_id, new_note_id);
	}
}

