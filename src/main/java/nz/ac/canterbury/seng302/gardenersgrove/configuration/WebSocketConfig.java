package nz.ac.canterbury.seng302.gardenersgrove.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Register the STOMP endpoints
     * @param registry The STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // For Test Environment
//        registry.addEndpoint("/test/ws").setAllowedOrigins("*");
        // For Product Environment
//        registry.addEndpoint("/prod/ws").setAllowedOrigins("*");
        // For Local Host
//        registry.addEndpoint("/ws")

        // New Approach
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
//                .withSockJS();
    }

    /**
     * Configure the message broker
     * @param registry The message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/", "/queue/");
        registry.setApplicationDestinationPrefixes("/app");
    }
}