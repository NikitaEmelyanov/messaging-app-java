package com.telegram.clone.common.model;

import com.telegram.clone.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Модель пользователя чата.
 * Содержит всю информацию о пользователе системы.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор пользователя
     */
    private String id;

    /**
     * Уникальное имя пользователя (логин)
     */
    private String username;

    /**
     * Хэш пароля пользователя
     */
    private String passwordHash;

    /**
     * Отображаемое имя пользователя
     */
    private String displayName;

    /**
     * Текущий статус пользователя
     */
    @Builder.Default
    private UserStatus status = UserStatus.OFFLINE;

    /**
     * Время последней активности
     */
    private LocalDateTime lastActiveTime;

    /**
     * Время создания аккаунта
     */
    private LocalDateTime createdAt;

    /**
     * Аватар пользователя (base64)
     */
    private String avatarBase64;

    /**
     * Проверка, является ли пользователь активным (онлайн)
     *
     * @return true если пользователь онлайн
     */
    public boolean isOnline() {
        return status == UserStatus.ONLINE;
    }

    /**
     * Обновить время последней активности
     */
    public void updateLastActive() {
        this.lastActiveTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return String.format("User{username='%s', status=%s}", username, status);
    }
}