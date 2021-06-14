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
		
	public User findByTeamAndId(Team team, Long id) {
		return userRepository.find("team = ?1 and id = ?2", team, id).firstResult();
	}
	
	public User findByName(User dto) {		
		return User.find("name", dto.name).firstResult();
	}
	
	public List<User> listByTeam(Team teamDTO) {		
		return User.list("team", teamDTO);
	}
	
	public Optional<User> findByIdOptional(Long id) {
		return userRepository.findByIdOptional(id);
	}
	
	public User create(User dto) throws Exception { 
		if(this.findByName(dto) != null) {
			throw new Exception("Element exists");
		}
		
		User user = new User();
		
		user.name = dto.name;
		user.key = dto.key;
		user.team = dto.team;
		
		userRepository.persist(user);	
		
		return user;
	}  
	
	public User update(Long id, User dto) {	
		Optional<User> userOptional = this.findByIdOptional(id);
		
		if (userOptional.isEmpty()) {
			throw new NotFoundException();
		}
		
		User user = userOptional.get();
		
		user.name = dto.name; 
		
		userRepository.persist(user);
		
		return user;
	}
	
	public void delete(Long idUser) {
		Optional<User> userOptional = this.findByIdOptional(idUser);
		
		userOptional.ifPresentOrElse(User::delete, () -> {
			throw new NotFoundException();
		});
	}

}
