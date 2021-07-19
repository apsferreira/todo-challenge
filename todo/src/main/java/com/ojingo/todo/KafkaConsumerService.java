package com.ojingo.todo;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojingo.todo.data.repositories.TeamRepository;
import com.ojingo.todo.data.repositories.TodoRepository;
import com.ojingo.todo.data.repositories.UserRepository;
import com.ojingo.todo.domain.entities.Team;
import com.ojingo.todo.domain.entities.User;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@ApplicationScoped
public class KafkaConsumerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
	
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private TodoRepository todoRepository;
    
    @Inject
    private TeamRepository teamRepository;

    @Incoming("keycloak-users")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessageKeycloakUsers(KafkaRecord<String, String> message) throws IOException {
        return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					LOGGER.info("receiving data");
					
					if (data != null && data.get("op").asText()	.equals("c")) {
						UUID newUserId = userRepository.createFromKafka(User.of(UUID.fromString(data.get("after").get("id").asText()), 
								data.get("after").get("username").asText(), data.get("after").get("email").asText())).await().indefinitely();
						
						UUID newTodoId = todoRepository.createFromKafka(newUserId, data.get("after").get("username").asText()).await().indefinitely();
						
						if (newTodoId != null) {
							LOGGER.info("user and todo created with success");
						}
					} else if (data != null && data.get("op").asText().equals("u")){
						if (userRepository.update(UUID.fromString(data.get("after").get("id").asText()), 
							User.of(UUID.fromString(data.get("after").get("id").asText()), 
								data.get("after").get("username").asText(), data.get("after").get("email").asText())).await().indefinitely()){						
							LOGGER.info("user updated with success");
						}
					} else if (data != null && data.get("op").asText().equals("d")){
						if(userRepository.delete(UUID.fromString(data.get("before").get("id").asText())).await().indefinitely() 
								&& todoRepository.deleteFromKafkaByUserId(UUID.fromString(data.get("before").get("id").asText())).await().indefinitely()) {
							LOGGER.info("user deleted with success");							
						}
					}					
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			message.ack();
        });
    }
    
    @Incoming("register-teams")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessageRegisterTeams(KafkaRecord<String, String> message) throws IOException {
    	return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					LOGGER.info("receiving data");
					
					if (data != null && data.get("op").asText()	.equals("c")) {
						Team registeredTeam = teamRepository.findByNameAndOriginalId(data.get("after").get("name").asText(), data.get("after").get("id").asLong()).await().indefinitely();
						
						if(registeredTeam != null && registeredTeam.getId() != null) {
							LOGGER.info("team exists");
						} else {
							teamRepository.createFromKafka(Team.of(data.get("after").get("name").asText(), data.get("after").get("id").asLong())).await().indefinitely();
							LOGGER.info("team created with success");							
						}
					} else if (data != null && data.get("op").asText().equals("u")){
						if (teamRepository.updateFromKafka(data.get("after").get("id").asLong(), Team.of(data.get("after").get("username").asText(), data.get("after").get("id").asLong()))
								.await().indefinitely()){						
							LOGGER.info("Team updated with success");
						}
					} else if (data != null && data.get("op").asText().equals("d")){
						if(teamRepository.delete(data.get("before").get("id").asLong()).await().indefinitely()) {
							LOGGER.info("Team deleted with success");							
						}
					}					
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			message.ack();
        });
    }
    
    @Incoming("register-users")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessageRegisterUsers(KafkaRecord<String, String> message) throws IOException {
    	return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					LOGGER.info("receiving data");

					if (data != null && data.get("op").asText().equals("u")){
						Team team = teamRepository.findByOriginalId(data.get("after").get("team_id").asLong()).await().indefinitely();
						User user = userRepository.findById(UUID.fromString(data.get("after").get("original_id").asText())).await().indefinitely();
						
						if (team != null) {
							user.setTeam(team);
							if (userRepository.updateTeam(user.getId(), user).await().indefinitely()) {
								LOGGER.info("User updated with success");								
							}
						}
					}					
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			message.ack();
        });
    }
}
