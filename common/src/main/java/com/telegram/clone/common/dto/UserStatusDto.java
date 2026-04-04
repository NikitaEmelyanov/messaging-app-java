package com.telegram.clone.common.dto;

import com.telegram.clone.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO для передачи информации о статусе пользователя.
 * Используется для уведомлений об изменении статуса.
 *
 * @param username имя пользователя
 * @param status новый статус пользователя
 * @param timestamp время изменения статуса
 */
@Schema(description = "DTO статуса пользователя")
public record UserStatusDto(

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Schema(description = "Имя пользователя",
        example = "alice",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @NotNull(message = "Статус не может быть пустым")
    @Schema(description = "Новый статус пользователя",
        example = "ONLINE",
        requiredMode = Schema.RequiredMode.REQUIRED)
    UserStatus status,

    @Schema(description = "Время изменения статуса",
        example = "2024-01-15T14:30:00")
    LocalDateTime timestamp

) {

    /**
     * Компактный конструктор с установкой времени
     */
    public UserStatusDto {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    /**
     * Создать DTO для онлайн статуса
     *
     * @param username имя пользователя
     * @return DTO со статусом ONLINE
     */
    public static UserStatusDto online(String username) {
        return new UserStatusDto(username, UserStatus.ONLINE, LocalDateTime.now());
    }

    /**
     * Создать DTO для оффлайн статуса
     *
     * @param username имя пользователя
     * @return DTO со статусом OFFLINE
     */
    public static UserStatusDto offline(String username) {
        return new UserStatusDto(username, UserStatus.OFFLINE, LocalDateTime.now());
    }

    /**
     * Создать DTO для статуса "печатает"
     *
     * @param username имя пользователя
     * @return DTO со статусом TYPING
     */
    public static UserStatusDto typing(String username) {
        return new UserStatusDto(username, UserStatus.TYPING, LocalDateTime.now());
    }

    /**
     * Проверка, изменился ли статус на онлайн
     *
     * @return true если статус ONLINE
     */
    public boolean isOnline() {
        return status == UserStatus.ONLINE;
    }

    /**
     * Проверка, изменился ли статус на оффлайн
     *
     * @return true если статус OFFLINE
     */
    public boolean isOffline() {
        return status == UserStatus.OFFLINE;
    }

    /**
     * Проверка, печатает ли пользователь
     *
     * @return true если статус TYPING
     */
    public boolean isTyping() {
        return status == UserStatus.TYPING;
    }

    @Override
    public String toString() {
        return String.format("UserStatusDto{username='%s', status=%s, timestamp=%s}",
            username, status, timestamp);
    }
}