package com.telegram.clone.server.config;

import com.telegram.clone.server.controller.ChatWebSocketController;
import com.telegram.clone.server.interceptor.AuthHandshakeInterceptor;
import com.telegram.clone.server.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketController chatWebSocketController;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketController, "/ws/chat")
            .setAllowedOrigins("*")
            .addInterceptors(authHandshakeInterceptor, loggingInterceptor)
            .setHandshakeHandler(new DefaultHandshakeHandler());

        registry.addHandler(chatWebSocketController, "/ws/status")
            .setAllowedOrigins("*")
            .addInterceptors(authHandshakeInterceptor, loggingInterceptor);
    }
}