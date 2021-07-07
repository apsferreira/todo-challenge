package com.ojingo.register.data.repositories;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.entities.Team;

import io.quarkus.hibernate.orm.panache.PanacheRepository;


@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {

	public boolean isNew(Team team) {
		if(this.find("name", team.name).count() > 0) {
			return false;
		}
		
		return true;
	}
	
	public Team create(Team team) {
		if (this.isNew(team)) {
			this.persist(team);
		}
		
		return team;		
	}  
	
	public Team update(Team teamToUpdate, Team team) {
		if(team.name != null) {
			teamToUpdate.name = team.name;
		}
		
		this.persist(teamToUpdate);
		
		return teamToUpdate;
	}
}
