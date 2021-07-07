package com.ojingo.register.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.entities.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;


@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
	
	public User findByOriginalId(UUID originalId) {
		User user = this.find("originalId", originalId).firstResult();
		return user;
	}
	
	public User update(User userToUpdate, User user) {
		
		if(user.username != null) {
			userToUpdate.username = user.username;			
		}
		
		if(user.email != null) {
			userToUpdate.email = user.email;			
		}
		
		if(user.team != null) {						
			userToUpdate.team = user.team;
		}
		
		this.persist(userToUpdate);
		
		return userToUpdate;
	}
}
