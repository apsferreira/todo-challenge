package com.ojingo.register;

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
import com.ojingo.register.data.repositories.UserRepository;
import com.ojingo.register.domain.entities.User;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@ApplicationScoped
public class KafkaConsumerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
	
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Inject
    private UserRepository userRepository;
    
    @Incoming("keycloak-users")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessageUsers(KafkaRecord<String, String> message) throws IOException {
        return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					
					if (data != null && data.get("op").asText()	.equals("c")) {
						LOGGER.info("receiving data {} ", data.get("after"));
						
						userRepository.persist(User.of(UUID.fromString(data.get("after").get("id").asText()),data.get("after").get("username").asText(), data.get("after").get("email").asText()));
						
						LOGGER.info("user created with success");
					} else if (data != null && data.get("op").asText().equals("u")){
						LOGGER.info("receiving data {} ", data.get("after"));
						
						if (userRepository.update(userRepository.findByOriginalId(UUID.fromString(data.get("after").get("id").asText())), 
								User.of(UUID.fromString(data.get("after").get("id").asText()),data.get("after").get("username").asText(), data.get("after").get("email").asText())).isPersistent()){						
							LOGGER.info("user updated with success");
						}
					} else if (data != null && data.get("op").asText().equals("d")){
						LOGGER.info("receiving data {} ", data.get("before"));

						userRepository.delete(userRepository.findByOriginalId(UUID.fromString(data.get("after").get("id").asText()))); 
						
						LOGGER.info("user deleted with success");							
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
    public CompletionStage<Void> onMessageTeams(KafkaRecord<String, String> message) throws IOException {
    	return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					
									
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			message.ack();
        });
    }
}
