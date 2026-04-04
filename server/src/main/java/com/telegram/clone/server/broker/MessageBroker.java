package com.telegram.clone.server.broker;

import com.telegram.clone.common.dto.MessageDto;
import com.telegram.clone.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Брокер сообщений для управления WebSocket сессиями и рассылки сообщений.
 * Разрывает циклическую зависимость между контроллером и сервисом.
 */
@Slf4j
@Component
public class MessageBroker {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    /**
     * Зарегистрировать WebSocket сессию для пользователя.
     *
     * @param username имя пользователя
     * @param session WebSocket сессия
     */
    public void registerSession(String username, WebSocketSession session) {
        sessions.put(username, session);
        sessionToUser.put(session.getId(), username);
        log.info("Session registered for user: {}", username);
        log.debug("Total active sessions: {}", sessions.size());
    }

    /**
     * Удалить регистрацию сессии пользователя.
     *
     * @param username имя пользователя
     */
    public void unregisterSession(String username) {
        WebSocketSession session = sessions.remove(username);
        if (session != null) {
            sessionToUser.remove(session.getId());
            log.info("Session unregistered for user: {}", username);
        }
    }

    /**
     * Отправить сообщение конкретному пользователю.
     *
     * @param username имя получателя
     * @param message сообщение для отправки
     * @return true если сообщение отправлено успешно
     */
    public boolean sendToUser(String username, MessageDto message) {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                String json = JsonUtils.toJson(message);
                synchronized (session) {
                    session.sendMessage(new TextMessage(json));
                }
                log.debug("Message sent to user: {}", username);
                return true;
            } catch (IOException e) {
                log.error("Failed to send message to user: {}", username, e);
                return false;
            }
        }
        log.warn("Cannot send message to user {}: session not found or closed", username);
        return false;
    }

    /**
     * Отправить сообщение всем пользователям.
     *
     * @param message сообщение для рассылки
     * @param excludeUsername имя пользователя, которому не нужно отправлять
     * @return количество успешных отправок
     */
    public int broadcastToAll(MessageDto message, String excludeUsername) {
        int successCount = 0;
        String json;
        try {
            json = JsonUtils.toJson(message);
        } catch (Exception e) {
            log.error("Failed to serialize message for broadcast", e);
            return 0;
        }

        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (excludeUsername != null && entry.getKey().equals(excludeUsername)) {
                continue;
            }

            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(json));
                    }
                    successCount++;
                } catch (IOException e) {
                    log.error("Failed to broadcast message to user: {}", entry.getKey(), e);
                }
            }
        }

        log.debug("Message broadcast to {} users (excluded: {})", successCount, excludeUsername);
        return successCount;
    }

    /**
     * Получить количество активных соединений.
     *
     * @return количество активных сессий
     */
    public int getActiveConnectionsCount() {
        return sessions.size();
    }

    /**
     * Проверить, находится ли пользователь в сети.
     *
     * @param username имя пользователя
     * @return true если пользователь онлайн
     */
    public boolean isUserOnline(String username) {
        WebSocketSession session = sessions.get(username);
        return session != null && session.isOpen();
    }

    /**
     * Получить имя пользователя по ID сессии.
     *
     * @param sessionId ID WebSocket сессии
     * @return имя пользователя или null
     */
    public String getUsernameBySessionId(String sessionId) {
        return sessionToUser.get(sessionId);
    }

    /**
     * Закрыть все активные сессии.
     */
    public void closeAllSessions() {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            try {
                entry.getValue().close();
                log.info("Closed session for user: {}", entry.getKey());
            } catch (IOException e) {
                log.error("Failed to close session for user: {}", entry.getKey(), e);
            }
        }
        sessions.clear();
        sessionToUser.clear();
        log.info("All sessions closed");
    }
}