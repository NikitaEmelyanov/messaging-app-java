package com.telegram.clone.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Перехватчик для логирования WebSocket handshake.
 * Логирует все попытки подключения и их результаты.
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandshakeInterceptor {

    /**
     * Логирование перед handshake.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes) {

        log.info("WebSocket handshake initiated from: {}", request.getRemoteAddress());
        log.debug("Request URI: {}", request.getURI());
        log.debug("Request headers: {}", request.getHeaders());

        return true;
    }

    /**
     * Логирование после handshake.
     */
    @Override
    public void afterHandshake(ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception) {

        if (exception == null) {
            log.info("WebSocket handshake completed successfully from: {}",
                request.getRemoteAddress());
        } else {
            log.error("WebSocket handshake failed from: {}, error: {}",
                request.getRemoteAddress(), exception.getMessage(), exception);
        }
    }
}