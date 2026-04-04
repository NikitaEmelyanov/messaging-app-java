package com.telegram.clone.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Модель пользовательской сессии на сервере.
 * Хранит информацию об активной сессии пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный токен сессии
     */
    private String token;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Время создания сессии
     */
    private LocalDateTime createdAt;

    /**
     * Время последней активности
     */
    private LocalDateTime lastActivityAt;

    /**
     * Время истечения сессии
     */
    private LocalDateTime expiresAt;

    /**
     * Флаг активности сессии
     */
    @Builder.Default
    private boolean active = true;

    /**
     * IP адрес пользователя (опционально)
     */
    private String ipAddress;

    /**
     * User-Agent клиента (опционально)
     */
    private String userAgent;

    /**
     * Проверить, активна ли сессия.
     *
     * @return true если сессия активна и не истекла
     */
    public boolean isActive() {
        return active && expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * Обновить время последней активности.
     */
    public void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Продлить сессию на указанное количество часов.
     *
     * @param hours количество часов для продления
     */
    public void extendSession(long hours) {
        this.expiresAt = LocalDateTime.now().plusHours(hours);
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Инвалидировать сессию.
     */
    public void invalidate() {
        this.active = false;
        this.expiresAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("UserSession{username='%s', active=%s, expiresAt=%s}",
            username, active, expiresAt);
    }
}