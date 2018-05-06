package com.locdata.location.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // WebSocket end point
    private static final String WEBSOCKET_ENDPOINT = "/locationendpoint";

    /**
     * This is to enable a simple message broker and to configure
     * the filter destinations targeting the broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker( "/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register the websocket endpoint that the client (e.g. browser)
     * will connect to
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_ENDPOINT).withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration channelRegistration) {
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> converters) {
        return true;
    }

}