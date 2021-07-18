package com.ojingo.register.data.repositories;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.entities.Team;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamRepository.class);

	public boolean isNew(Team team) {
		LOGGER.info("register -> validating if team {0} is new before insert", team.name);
		
		if(this.find("name", team.name).count() > 0) {
			LOGGER.debug("register -> the team {0} existis", team.name);
			return false;
		}
		
		return true;
	}
	
	public Team create(Team team) {
		if (this.isNew(team)) {
			LOGGER.info("register -> creating a new team {0}", team.name);
			this.persist(team);
		}
		
		return team;		
	}  
	
	public Team update(Team teamToUpdate, Team team) {
		if(team.name != null) {
			teamToUpdate.name = team.name;
		}
		LOGGER.info("register -> updating a new team {0}", team.name);
		this.persist(teamToUpdate);
		
		return teamToUpdate;
	}
}
