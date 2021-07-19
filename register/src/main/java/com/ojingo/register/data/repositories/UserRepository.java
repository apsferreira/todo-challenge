package com.ojingo.register.data.repositories;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.transaction.Transactional;

import com.ojingo.register.domain.entities.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ApplicationScoped
@ActivateRequestContext
public class UserRepository implements PanacheRepository<User> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
	
	public User update(User userToUpdate, User user) {
		
		LOGGER.info("register -> updating team of user: {0}", userToUpdate.username);
		
		if(user.team != null) {						
			userToUpdate.team = user.team;
		}
		
		this.persist(userToUpdate);
		
		return userToUpdate;
	}
	
	@Transactional
	public void updateFromKafka(UUID originalId, User user) {
		
		LOGGER.info("register -> updating user: {0} from kafka", originalId);

		User userToUpdate = this.find("originalId", originalId).firstResult();
		
		if(userToUpdate != null) {
			if(user.username != null) {
				userToUpdate.username = user.username;			
			}
		
			if(user.email != null) {
				userToUpdate.email = user.email;			
			}
			
			this.persist(userToUpdate);
		}
	}
	
	@Transactional
	public void deleteFromKafka(UUID originalId) {
		
		LOGGER.info("register -> deleting user: {0} from kafka", originalId);

		User user = this.find("originalId", originalId).firstResult();
		
		if (user != null) {
			this.delete(user);
		}
	}

	@Transactional
	public void createFromKafka(User user) {
		LOGGER.info("register -> creating user: {0} from kafka", user.username);
		this.persist(user);
	}
	
	
}
