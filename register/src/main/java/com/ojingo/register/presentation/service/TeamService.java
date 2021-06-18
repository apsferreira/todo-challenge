package com.ojingo.register.presentation.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import com.ojingo.register.data.repository.TeamRepository;
import com.ojingo.register.domain.models.Team;

@ApplicationScoped
public class TeamService {
	
	@Inject 
	TeamRepository teamRepository;
	
	public List<Team> listAll() {
		return teamRepository.listAll();
	}
	
	public Team findById(Long id) {
		return teamRepository.findById(id);
	}
	
	public Optional<Team> findByIdOptional(Long id) {
		return teamRepository.findByIdOptional(id);
	}

	public List<Team> listByName(String name) {		
		return teamRepository.list("name", name);
	}
	
	public Team create(Team team) throws Exception { 
		if(this.listByName(team.name).size() > 0) {
			throw new IllegalArgumentException("The value is already in the list.");
		}		
		
		teamRepository.persist(team);	
		
		return team;		
	}  
	
	public Team update(Team teamToUpdate, Team team) {
		teamToUpdate.name = team.name; 
		
		teamRepository.persist(teamToUpdate);
		
		return teamToUpdate;
	}
	
	public void delete(Team team) {
		teamRepository.delete(team);
	}
}
