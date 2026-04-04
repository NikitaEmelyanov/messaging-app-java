package com.telegram.clone.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO для ответа на запрос аутентификации.
 * Содержит результат авторизации и информацию о пользователе.
 *
 * @param success флаг успешности аутентификации
 * @param token токен сессии (при успехе)
 * @param username имя пользователя
 * @param displayName отображаемое имя
 * @param message сообщение об ошибке или приветствие
 * @param timestamp время ответа
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO ответа на запрос аутентификации")
public record AuthResponseDto(

    @Schema(description = "Флаг успешной аутентификации",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED)
    boolean success,

    @Schema(description = "Токен сессии для последующих запросов",
        example = "eyJhbGciOiJIUzI1NiIs...")
    String token,

    @Schema(description = "Имя пользователя",
        example = "alice")
    String username,

    @Schema(description = "Отображаемое имя пользователя",
        example = "Alice Wonderland")
    String displayName,

    @Schema(description = "Сообщение сервера (приветствие или ошибка)",
        example = "Добро пожаловать, Alice!")
    String message,

    @Schema(description = "Время ответа сервера",
        example = "2024-01-15T14:30:00")
    LocalDateTime timestamp

) {

    /**
     * Создать успешный ответ аутентификации
     *
     * @param username имя пользователя
     * @param displayName отображаемое имя
     * @param token токен сессии
     * @return успешный ответ
     */
    public static AuthResponseDto success(String username, String displayName, String token) {
        return new AuthResponseDto(
            true,
            token,
            username,
            displayName,
            "Добро пожаловать, " + (displayName != null ? displayName : username) + "!",
            LocalDateTime.now()
        );
    }

    /**
     * Создать ответ об ошибке аутентификации
     *
     * @param errorMessage сообщение об ошибке
     * @return ответ с ошибкой
     */
    public static AuthResponseDto error(String errorMessage) {
        return new AuthResponseDto(
            false,
            null,
            null,
            null,
            errorMessage,
            LocalDateTime.now()
        );
    }

    /**
     * Создать ответ о неудачной аутентификации (неверные учетные данные)
     *
     * @return ответ с ошибкой
     */
    public static AuthResponseDto invalidCredentials() {
        return error("Неверное имя пользователя или пароль");
    }

    /**
     * Проверка успешности аутентификации
     *
     * @return true если аутентификация успешна
     */
    public boolean isAuthenticated() {
        return success && token != null && !token.isBlank();
    }
}