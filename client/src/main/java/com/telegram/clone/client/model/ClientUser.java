package com.telegram.clone.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Модель пользователя на клиенте.
 * Содержит информацию о текущем пользователе и его сессии.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUser {

    /** Уникальное имя пользователя (логин) */
    private String username;

    /** Отображаемое имя пользователя */
    private String displayName;

    /** Токен аутентификации для сессии */
    private String token;

    /** Флаг онлайн статуса */
    private boolean online;

    /** Время последней активности пользователя */
    private LocalDateTime lastActiveTime;

    /** Аватар пользователя в формате base64 */
    private String avatarBase64;

    /**
     * Возвращает отображаемое имя пользователя.
     * Если отображаемое имя не задано, возвращает логин.
     *
     * @return отображаемое имя или логин
     */

    public String getDisplayName() {
        return displayName != null && !displayName.isEmpty() ? displayName : username;
    }

    /**
     * Создает пользователя из ответа сервера при успешной аутентификации.
     *
     * @param username имя пользователя
     * @param displayName отображаемое имя
     * @param token токен сессии
     * @return новый объект ClientUser
     */
    public static ClientUser fromAuthResponse(String username, String displayName, String token) {
        ClientUser user = new ClientUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setToken(token);
        user.setOnline(true);
        user.setLastActiveTime(LocalDateTime.now());
        return user;
    }

    /**
     * Возвращает строковое представление пользователя (отображаемое имя).
     *
     * @return отображаемое имя пользователя
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}