package com.sparta.gathering.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 구독용 엔드포인트
        config.setApplicationDestinationPrefixes("/app"); // 메시지 송신용 엔드포인트
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws/chat" 엔드포인트 등록 및 SockJS 지원
        registry.addEndpoint("/ws/chat").setAllowedOriginPatterns("*").withSockJS();
    }
}
