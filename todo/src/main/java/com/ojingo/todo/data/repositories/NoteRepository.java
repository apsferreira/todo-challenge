package com.ojingo.todo.data.repositories;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.domain.entities.Note;
import com.ojingo.todo.domain.entities.Todo;
import com.ojingo.todo.domain.entities.User;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

@ApplicationScoped
public class NoteRepository {
	
	@Inject
	private PgPool client;
	
	private String filter;
	
	private String sort;

	private static final Logger LOGGER = LoggerFactory.getLogger(NoteRepository.class);
	
	private static final String NOT_VERSIONED_NOTES = "select \n"
														+ "	n1.id,\n"
														+ "	n1.description,\n"
														+ "	n1.done,\n"
														+ "	n1.favorite,\n"
														+ "	n1.todo_id,\n"
														+ "	n1.user_id,\n"
														+ " u1.username,\n"
														+ "	n1.created_at,\n"
														+ "	n1.subject_jwt\n"
														+ "from \n"
														+ "	notes n1\n"
														+ " inner join\n"
														+ "	users u1\n"
														+ "	on \n"
														+ "	n1.user_id = u1.id "
														+ "where \n"
														+ "	n1.id not in (\n"
														+ "		select \n"
														+ "			v1.note_parent_id \n"
														+ "		from \n"
														+ "			versions v1\n"
														+ "	)\n"
														+ "	and \n"
														+ "	n1.id not in (\n"
														+ "		select \n"
														+ "			v1.new_note_id \n"
														+ "		from \n"
														+ "			versions v1\n"
														+ "	)\n";
	
	private static final String VERSIONED_NOTES = "select \n"
													+ "	n1.id,\n"
													+ "	n1.description,\n"
													+ "	n1.done,\n"
													+ "	n1.favorite,\n"
													+ "	n1.todo_id,\n"
													+ "	n1.user_id,\n"
													+ " u1.username,\n"
													+ "	n1.created_at,\n"
													+ "	n1.subject_jwt\n"
													+ "from\n"
													+ "	notes n1\n"
													+ " inner join\n"
													+ "	users u1\n"
													+ "	on \n"
													+ "	n1.user_id = u1.id "
													+ "	inner join\n"
													+ "	versions v1\n"
													+ "	on \n"
													+ "		n1.id = v1.new_note_id \n"
													+ "	inner join \n"
													+ "	(\n"
													+ "		select\n"
													+ "			v2.note_parent_id,\n"
													+ "			max(v2.version) as version\n"
													+ "		from \n"
													+ "			versions v2\n"
													+ "		group by\n"
													+ "			v2.note_parent_id\n"
													+ "	) as v2 \n"
													+ "	on \n"
													+ "	v1.version = v2.version\n"
													+ "	where true \n";
	
	private void configureFilter(List<String> filter, String sort) {
		this.filter = "";
		this.sort = " order by ";
		
		if (sort != null) {
			switch(sort){
				case "star":
					this.sort += " favorite DESC";
					break;
				case "done":
					this.sort += " done DESC";
					break;
				case "creation_date":
					this.sort += " created_at ASC";
					break;
				default:
					this.sort += " description ASC";
			}			
		} else {
			this.sort += " description ASC";
		}
		
		if(filter != null && filter.size() > 0) {
			switch(filter.get(0)){
				case "author":
					this.filter = " and lower(u1.name) like '%" + filter.get(1).toLowerCase() + "%'\n";
					break;
				case "creation_date":
					this.filter = "and n1.created_at = " + filter.get(1);
					break;
				case "done":
					this.filter = " and n1.done = true \n" ;
					break;
				case "favorite":
					this.filter = " and n1.favorite = true \n";
					break;
				default:
					this.filter = "";
			}
		}
	}
	
	public Multi<Note> listAll(List<String> filter, String sort) {
		this.configureFilter(filter, sort);
		
		LOGGER.info("Listing all notes by filters and sort");
		
		return this.client
				.query(NOT_VERSIONED_NOTES  
						+ this.filter 
						+ " union\n " 
						+ VERSIONED_NOTES 
						+ this.filter
						+ this.sort).execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(note -> this.fromNote(note));
    }

	public Uni<Note> findById(UUID id) {
		LOGGER.info("finding note by id: {0}", id);
		
        return this.client
        		.preparedQuery("SELECT * FROM notes WHERE id = $1").execute(Tuple.of(id))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromNote(iterator.next()) : null);
    }
	
	public Multi<Note> listByUser(UUID idUser, List<String> filter, String sort) {
		this.configureFilter(filter, sort);
		
		LOGGER.info("Listing notes by user: {0}", idUser);
		
		return this.client
				.query(NOT_VERSIONED_NOTES
						+ "	and n1.user_id = '" + idUser + "'"
						+ this.filter 
						+ " union\n "
						+ VERSIONED_NOTES
						+ this.filter 
						+ " and	n1.user_id = '" + idUser + "'"
						+ this.sort) 
					.execute().onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
					.onItem().transform(set -> this.fromNote(set)); 			
    }
    
    public Multi<Note> listByTodo(UUID idTodo, List<String> filter, String sort) {
		this.configureFilter(filter, sort);
		
		LOGGER.info("Listing notes by todo:: {0}", idTodo);
		
        return this.client
        		.query(NOT_VERSIONED_NOTES
						+ "	and n1.todo_id = '" + idTodo + "'"
						+ this.filter 
						+ " union\n "
						+ VERSIONED_NOTES
						+ this.filter
						+ " and n1.todo_id = '" + idTodo + "'"
						+ this.sort)
                .execute().onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(set -> this.fromNote(set));
    }
    
    public Uni<UUID> create(Note note, String subjectJWT) {
    	LOGGER.info("Creating a new note");
    	
    	return this.client
    		.preparedQuery("INSERT INTO notes (user_id, todo_id, description, done, favorite, subject_jwt) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id;")
    		.execute(Tuple.of(note.getUser().getId(), note.getTodo().getId(), note.getDescription(), note.isDone(), note.isFavorite(), subjectJWT))
    			.onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));    	
    }
    
    public Uni<Boolean> updateDone (UUID id, Note note) {
    	LOGGER.info("Updating Done of note: {0}", id);
    	
    	return this.client
			.preparedQuery("update notes set done = $1 where id = $2;")
			.execute(Tuple.of(note.isDone(), id))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> updateFavorite (UUID id, Note note) {
    	LOGGER.info("Updating Favorite of note: {0}", id);
    	
    	return this.client
			.preparedQuery("update notes set favorite = $1 where id = $2;")
			.execute(Tuple.of(note.isFavorite(), id))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> delete (UUID id) {
    	LOGGER.info("Deleting note: {0}", id);

    	return this.client
			.preparedQuery("DELETE FROM notes where id = $1;")
			.execute(Tuple.of(id))
	            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

	public Note fromNote(Row row) {
		User user = User.of(row.getUUID("user_id"));			
		Todo todo = Todo.of(row.getUUID("todo_id"));			
		return Note.of(row.getUUID("id"), row.getString("description"), row.getBoolean("done"), row.getBoolean("favorite"), row.getOffsetDateTime("created_at"), user, todo, row.getString("subject_jwt"));
	}
}
