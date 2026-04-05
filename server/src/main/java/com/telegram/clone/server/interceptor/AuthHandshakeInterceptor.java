package com.telegram.clone.server.interceptor;

import com.telegram.clone.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Перехватчик для аутентификации WebSocket handshake.
 * Проверяет токен аутентификации при установке WebSocket соединения.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthService authService;

    private static final String TOKEN_PARAM = "token";
    private static final String USERNAME_ATTR = "username";

    /**
     * Обработка перед handshake.
     * Извлекает токен из запроса и проверяет его валидность.
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param wsHandler WebSocket обработчик
     * @param attributes атрибуты для передачи в WebSocket сессию
     * @return true если handshake разрешен
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes) {

        String token = extractToken(request);

        if (token == null || !authService.validateToken(token)) {
            log.warn("WebSocket handshake rejected: invalid or missing token");
            return false;
        }

        String username = authService.getUsernameByToken(token);
        if (username == null) {
            log.warn("WebSocket handshake rejected: username not found for token");
            return false;
        }

        // Сохраняем username в атрибуты сессии
        attributes.put(USERNAME_ATTR, username);
        log.info("WebSocket handshake successful for user: {}", username);

        return true;
    }

    /**
     * Обработка после handshake.
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param wsHandler WebSocket обработчик
     * @param exception исключение (если было)
     */
    @Override
    public void afterHandshake(ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        }
    }

    /**
     * Извлечь токен из запроса.
     * Токен может быть в query параметре или в заголовке Authorization.
     *
     * @param request HTTP запрос
     * @return токен или null
     */
    private String extractToken(ServerHttpRequest request) {
        // Пробуем извлечь из query параметра
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter(TOKEN_PARAM);
            if (token != null && !token.isEmpty()) {
                return token;
            }
        }

        // Пробуем извлечь из заголовка Authorization
        var headers = request.getHeaders();
        String authHeader = headers.getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}