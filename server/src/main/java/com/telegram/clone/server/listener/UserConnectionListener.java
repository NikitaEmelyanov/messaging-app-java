package com.telegram.clone.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Слушатель событий подключения/отключения пользователей (паттерн Observer).
 * Реагирует на события WebSocket сессий.
 */
@Slf4j
@Component
public class UserConnectionListener {

    /**
     * Обработка события подключения.
     *
     * @param event событие подключения
     */
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        log.info("User connected. Session ID: {}", event.getUser() != null ?
            event.getUser().getName() : "unknown");

        // Можно добавить дополнительную логику при подключении
        // Например, загрузку истории сообщений
    }

    /**
     * Обработка события отключения.
     *
     * @param event событие отключения
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("User disconnected. Session ID: {}", sessionId);

        // Можно добавить дополнительную логику при отключении
        // Например, сохранение непрочитанных сообщений
    }
}