package com.telegram.clone.server.factory;

import com.telegram.clone.server.model.UserSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Фабрика для создания объектов сессий (паттерн Factory).
 * Упрощает создание новых сессий с правильными значениями по умолчанию.
 */
@Component
public class SessionFactory {

    private static final long SESSION_DURATION_HOURS = 24;

    /**
     * Создать новую сессию для пользователя.
     *
     * @param username имя пользователя
     * @return новая сессия
     */
    public UserSession createSession(String username) {
        String token = generateToken(username);

        return UserSession.builder()
            .token(token)
            .username(username)
            .createdAt(LocalDateTime.now())
            .lastActivityAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS))
            .active(true)
            .build();
    }

    /**
     * Создать сессию с кастомной длительностью.
     *
     * @param username имя пользователя
     * @param durationHours длительность в часах
     * @return новая сессия
     */
    public UserSession createSession(String username, long durationHours) {
        String token = generateToken(username);

        return UserSession.builder()
            .token(token)
            .username(username)
            .createdAt(LocalDateTime.now())
            .lastActivityAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusHours(durationHours))
            .active(true)
            .build();
    }

    /**
     * Создать восстановленную сессию из токена.
     *
     * @param token токен сессии
     * @param username имя пользователя
     * @return восстановленная сессия
     */
    public UserSession restoreSession(String token, String username) {
        return UserSession.builder()
            .token(token)
            .username(username)
            .createdAt(LocalDateTime.now().minusHours(1))
            .lastActivityAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS))
            .active(true)
            .build();
    }

    /**
     * Генерация уникального токена.
     *
     * @param username имя пользователя для включения в токен
     * @return сгенерированный токен
     */
    private String generateToken(String username) {
        return UUID.randomUUID().toString() + "-" +
               System.currentTimeMillis() + "-" +
               username.hashCode();
    }
}