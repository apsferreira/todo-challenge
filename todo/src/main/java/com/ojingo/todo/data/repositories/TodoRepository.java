package com.ojingo.todo.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.domain.entities.Team;
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
public class TodoRepository {
	
	@Inject
	private PgPool client;

	private static final Logger LOGGER = LoggerFactory.getLogger(TodoRepository.class);
	
	public Multi<Todo> listAll() {
		return this.client
				.query(" SELECT "
						+ " t.id as todo_id, t.description as todo_description, "
						+ " u.id as user_id, u.username as user_name, "
						+ " te.id as team_id, te.name as team_name "
						+ " FROM todos t LEFT JOIN users u on t.user_id = u.id LEFT JOIN teams te "
						+ " on t.team_id = te.id ").execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(todo -> this.fromTodoJoin(todo));
    }
	
	public Uni<Todo> findById(UUID id) {
        return this.client
        		.preparedQuery("SELECT * FROM todos WHERE id = $1").execute(Tuple.of(id))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromTodo(iterator.next()) : null);
    }
	
	public Uni<Todo> findByUser(UUID idUser) {
		return this.client
				.preparedQuery("SELECT * FROM todos WHERE user_id = $1").execute(Tuple.of(idUser)) 
				.onItem().transform(RowSet::iterator) 
				.onItem().transform(iterator -> iterator.hasNext() ? fromTodo(iterator.next()) : null);
    }
    
    public Multi<Todo> findByTeam(UUID idTeam) {
        return this.client
                .preparedQuery("SELECT id, description FROM todos t where t.team_id = $1 ORDER BY id ASC")
                .execute(Tuple.of(idTeam)).onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(set -> this.fromTodo(set));
    }
    
    public Uni<UUID> createByTeam(Todo todo) {
    	LOGGER.info("Creating a new todo by team: " + todo.getTeam().getId());
    	
    	return this.client
    		.preparedQuery("INSERT INTO todos (description, team_id) VALUES ($1, $2) RETURNING id;")
    		.execute(Tuple.of(todo.getDescription(), todo.getTeam().getId())).onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));    	
    }

    public Uni<UUID> createByUser(Todo todo) {
    	LOGGER.info("Creating a new todo by user: " + todo.getUser().getId());
    	
    	return this.client
			.preparedQuery("INSERT INTO todos (description, user_id) VALUES ($1, $2) RETURNING id;")
			.execute(Tuple.of(todo.getDescription(), todo.getUser().getId())).onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));
    }
    
    public Uni<Boolean> update (UUID id, Todo todo) {
    	LOGGER.info("Updating todo " + id);
    	
    	return this.client
			.preparedQuery("update todos set description = $1 where id = $2;")
			.execute(Tuple.of(todo.getDescription(), id))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> delete (UUID id) {
    	LOGGER.info("Deleting todo " + id);

    	return this.client
			.preparedQuery("DELETE FROM todos where id = $1;")
			.execute(Tuple.of(id))
	            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

	public Todo fromTodo(Row row) {		
		Team team = Team.of(row.getUUID("team_id"), null);
		User user = User.of(row.getUUID("user_id"), null, null, team);
		return Todo.of(row.getUUID("id"), row.getString("description"), user, team);
	}
	
	public Todo fromTodoJoin(Row row) {
		Team team = Team.of(row.getUUID("team_id"), row.getString("team_name"));			
		User user = User.of(row.getUUID("user_id"), row.getString("user_name"), null, team);			
		return Todo.of(row.getUUID("todo_id"), row.getString("todo_description"), user, team);
	}
}
