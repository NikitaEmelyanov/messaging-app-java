package com.telegram.clone.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Модель подключенного пользователя.
 * Хранит информацию о пользователе в реальном времени.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedUser {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * WebSocket session ID
     */
    private String sessionId;

    /**
     * Время подключения
     */
    private LocalDateTime connectedAt;

    /**
     * Время последней активности
     */
    private LocalDateTime lastHeartbeat;

    /**
     * Статус "печатает"
     */
    @Builder.Default
    private boolean typing = false;

    /**
     * Текущий чат (с кем ведется диалог)
     */
    private String currentChatWith;

    /**
     * Обновить heartbeat.
     */
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }

    /**
     * Установить статус печатания.
     *
     * @param typing true если пользователь печатает
     */
    public void setTyping(boolean typing) {
        this.typing = typing;
        this.lastHeartbeat = LocalDateTime.now();
    }

    /**
     * Проверить, активен ли пользователь.
     * Считается активным, если последний heartbeat был не более 30 секунд назад.
     *
     * @return true если активен
     */
    public boolean isActive() {
        return lastHeartbeat != null &&
               lastHeartbeat.plusSeconds(30).isAfter(LocalDateTime.now());
    }

    /**
     * Получить время подключения в удобном формате.
     *
     * @return строка с временем подключения
     */
    public String getConnectionTimeFormatted() {
        if (connectedAt == null) return "Unknown";

        var duration = java.time.Duration.between(connectedAt, LocalDateTime.now());
        long minutes = duration.toMinutes();

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";

        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";

        return (hours / 24) + " days ago";
    }

    @Override
    public String toString() {
        return String.format("ConnectedUser{username='%s', sessionId='%s', active=%s}",
            username, sessionId, isActive());
    }
}