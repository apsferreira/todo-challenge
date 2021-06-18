package com.ojingo.register.presentation.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import com.ojingo.register.data.repository.UserRepository;
import com.ojingo.register.domain.models.Team;
import com.ojingo.register.domain.models.User;

@ApplicationScoped
public class UserService {
	
	@Inject 
	UserRepository userRepository;
	
	public List<User> listAll() {
		return userRepository.listAll();
	}
	
	public User findById(Long id) {
		return userRepository.findById(id);
	}
		
	public Optional<User> findByIdOptional(Long id) {
		return userRepository.findByIdOptional(id);
	}
	
	public User findByIdTeamAndId(Long idTeam, Long id) {
		return userRepository.find("team.id = ?1 and id = ?2", idTeam, id).firstResult();
	}
	
	public User findByName(User user) {		
		return User.find("name", user.name).firstResult();
	}
	
	public List<User> listByTeam(Team team) {		
		return User.list("team", team);
	}
	
	public User create(User user) throws Exception { 
		if(this.findByName(user) != null) {
			throw new Exception("Element exists");
		}
		
		userRepository.persist(user);	
		
		return user;
	}  
	
	public User update(User userToUpdate, User user) {	
		userToUpdate.name = user.name; 
		
		userRepository.persist(userToUpdate);
		
		return userToUpdate;
	}
	
	public void delete(User user) {
		userRepository.delete(user);
	}

}
