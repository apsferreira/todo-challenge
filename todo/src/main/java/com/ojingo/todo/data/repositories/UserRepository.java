package com.ojingo.todo.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.domain.entities.Team;
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
public class UserRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
	
	@Inject
	private PgPool client;
		 
	public Multi<User> listAll() {
		LOGGER.info("List all users ");
		
		return this.client
				.query(" SELECT * FROM users ").execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(todo -> this.fromUser(todo));
    }
	
	public Uni<User> findById(UUID id) {
		LOGGER.info("Find by user Id: " + id);
		
        return this.client
        		.preparedQuery("SELECT * FROM users WHERE id = $1").execute(Tuple.of(id))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromUser(iterator.next()) : null);
    }
    
    public Multi<User> findByTeam(UUID idTeam) {
    	LOGGER.info("Find by Team Id: " + idTeam);
    	
        return this.client
                .preparedQuery("SELECT * FROM users u where u.team_id = $1 ORDER BY id ASC")
                .execute(Tuple.of(idTeam)).onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(set -> this.fromUser(set));
    }
    
    public Uni<UUID> createFromKafka(User user) {
    	LOGGER.info("Creating a new user");
    	
    	return this.client
    		.preparedQuery("INSERT INTO users (id, username, email) VALUES ($1, $2, $3) RETURNING id;")
    		.execute(Tuple.of(user.getId(), user.getUsername(), user.getEmail()))
    				.onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));    	
    }
    
    public Uni<Boolean> updateTeam(UUID id, User user) {
    	LOGGER.info("updating a user");
    	
    	return this.client
    		.preparedQuery("UPDATE users SET team_id = $1 where id = $2;")
    		.execute(Tuple.of(user.getTeam().getId(), id))
			.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> update(UUID id, User user) {
    	LOGGER.info("updating a user");
    	
    	return this.client
    		.preparedQuery("UPDATE users SET username = $1, email = $2 where id = $3;")
    		.execute(Tuple.of(user.getUsername(), user.getEmail(), id))
			.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> delete (UUID id) {
    	LOGGER.info("Deleting user " + id);

    	return this.client
			.preparedQuery("DELETE FROM users where id = $1;")
			.execute(Tuple.of(id))
	            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }
        
   	public User fromUser(Row row) {
		Team team = Team.of(row.getUUID("user_id"), null);
		return User.of(row.getUUID("id"), row.getString("username"), row.getString("email"), team);
	}
}
