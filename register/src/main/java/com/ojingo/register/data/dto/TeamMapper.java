package com.ojingo.register.data.dto;

import org.mapstruct.Mapper;

import com.ojingo.register.domain.models.Team;

@Mapper(componentModel = "cdi")
public interface TeamMapper {
	
	Team convertToTeam(TeamDTO teamDTO);
	
	Team convertToTeam(CreateTeamDTO createTeamDTO);
	
	Team convertToTeam(UpdateTeamDTO UpdateTeamDTO);

	TeamDTO convertToTeamDTO(Team team);
}