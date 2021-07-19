package com.ojingo.register;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
    @Transactional
    public CompletionStage<Void> onMessageUsers(KafkaRecord<String, String> message) throws IOException {
        return CompletableFuture.runAsync(() -> {                    		
			try {
				if (message != null && message.getPayload() != null) {
					JsonNode data = objectMapper.readValue(message.getPayload(), JsonNode.class);
					LOGGER.info("receiving data");
					
					if (data != null && data.get("op").asText()	.equals("c")) {
						userRepository.createFromKafka(User.of(UUID.fromString(data.get("after").get("id").asText()),data.get("after").get("username").asText(), data.get("after").get("email").asText()));
						LOGGER.info("user created with success");
					} else if (data != null && data.get("op").asText().equals("u")){
						userRepository.updateFromKafka(UUID.fromString(data.get("after").get("id").asText()), 
								User.of(UUID.fromString(data.get("after").get("id").asText()),data.get("after").get("username").asText(), 
										data.get("after").get("email").asText()));						
						LOGGER.info("user updated with success");
						
					} else if (data != null && data.get("op").asText().equals("d")){
						userRepository.deleteFromKafka(UUID.fromString(data.get("before").get("id").asText())); 						
						LOGGER.info("user deleted with success");							
					}					
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			message.ack();
        });
    }
}
