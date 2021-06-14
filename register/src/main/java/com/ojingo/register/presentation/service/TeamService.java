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
	
	public List<Team> listByName(String name) {		
		return teamRepository.list("name", name);
	}
	
	public Optional<Team> findByIdOptional(Long id) {
		return teamRepository.findByIdOptional(id);
	}
	
	public Team create(Team dto) throws Exception { 
		if(this.listByName(dto.name).size() > 0) {
			throw new IllegalArgumentException("The value is already in the list.");
		}
		
		Team team = new Team();
		
		team.name = dto.name;		
		
		teamRepository.persist(team);	
		
		return team;		
	}  
	
	public Team update(Long id, Team dto) {	
		Optional<Team> teamOptional = this.findByIdOptional(id);
		
		if (teamOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		Team team = teamOptional.get();
		
		team.name = dto.name; 
		
		teamRepository.persist(team);
		
		return team;
	}
	
	public void delete(Long id) {
		Optional<Team> teamOptional = this.findByIdOptional(id);
		
		teamOptional.ifPresentOrElse(Team::delete, () -> {
			throw new NotFoundException();
		});
	}

}
