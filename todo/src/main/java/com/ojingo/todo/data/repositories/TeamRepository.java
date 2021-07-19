package com.ojingo.todo.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ojingo.todo.domain.entities.Team;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

@ApplicationScoped
public class TeamRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamRepository.class);
	
	@Inject
	private PgPool client;
		 
	public Multi<Team> listAll() {
		LOGGER.info("List all users ");
		
		return this.client
				.query(" SELECT * FROM teams ").execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(todo -> this.fromTeam(todo));
    }
	
	public Uni<Team> findById(UUID id) {
		LOGGER.info("Find by user Id: {0}", id);
		
        return this.client
        		.preparedQuery("SELECT * FROM teams WHERE id = $1").execute(Tuple.of(id))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromTeam(iterator.next()) : null);
    }
	
	public Uni<Team> findByNameAndOriginalId(String name, Long originalId) {
		LOGGER.info("Find by team name: {0} and originalId: {1}", name, originalId);
		
        return this.client
        		.preparedQuery("SELECT * FROM teams WHERE name = $1 and original_id = $2").execute(Tuple.of(name, originalId))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromTeam(iterator.next()) : null);
    }
	
	public Uni<Team> findByOriginalId(long originalId) {
		 LOGGER.info("Find by and originalId: {0}", originalId);
		
        return this.client
        		.preparedQuery("SELECT * FROM teams WHERE original_id = $1").execute(Tuple.of(originalId))
                .map(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromTeam(iterator.next()) : null);
	}
    
    
    public Uni<UUID> createFromKafka(Team team) {
    	LOGGER.info("Creating a new Team");
    	
    	return this.client
    		.preparedQuery("INSERT INTO teams (name, original_id) VALUES ($1, $2) RETURNING id;")
    		.execute(Tuple.of(team.getName(), team.getOriginalId()))
    				.onItem().transform(pgRowSet -> pgRowSet.iterator().next().getUUID("id"));    	
    }
    
    public Uni<Boolean> updateFromKafka(Long originalId, Team team) {
    	LOGGER.info("updating a team");
    	
    	return this.client
    		.preparedQuery("UPDATE team SET name = $1 where original_id = $2;")
    		.execute(Tuple.of(team.getName(), originalId))
			.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);					
    }
    
    public Uni<Boolean> delete (Long id) {
    	LOGGER.info("Deleting team: {0}", id);

    	return this.client
			.preparedQuery("DELETE FROM teams where original_id = $1;")
			.execute(Tuple.of(id))
	            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }
        
   	public Team fromTeam(Row row) {
		return Team.of(row.getUUID("id"), row.getString("name"));
	}
}
