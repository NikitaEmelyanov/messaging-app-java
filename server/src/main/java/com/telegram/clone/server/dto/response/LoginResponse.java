package com.telegram.clone.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO ответа на запрос входа в систему.
 *
 * @param success флаг успешности
 * @param token токен сессии
 * @param username имя пользователя
 * @param displayName отображаемое имя
 * @param message сообщение сервера
 */
@Schema(description = "Ответ на запрос аутентификации")
public record LoginResponse(

    @Schema(description = "Флаг успешной аутентификации",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED)
    boolean success,

    @Schema(description = "Токен сессии для последующих запросов",
        example = "abc123-def456-ghi789")
    String token,

    @Schema(description = "Имя пользователя",
        example = "alice")
    String username,

    @Schema(description = "Отображаемое имя пользователя",
        example = "Alice Wonderland")
    String displayName,

    @Schema(description = "Сообщение сервера",
        example = "Добро пожаловать, Alice!")
    String message

) {
    /**
     * Создать успешный ответ.
     *
     * @param username имя пользователя
     * @param displayName отображаемое имя
     * @param token токен сессии
     * @return успешный ответ
     */
    public static LoginResponse success(String username, String displayName, String token) {
        return new LoginResponse(
            true,
            token,
            username,
            displayName,
            "Welcome, " + (displayName != null ? displayName : username) + "!"
        );
    }

    /**
     * Создать ответ с ошибкой.
     *
     * @param errorMessage сообщение об ошибке
     * @return ответ с ошибкой
     */
    public static LoginResponse error(String errorMessage) {
        return new LoginResponse(false, null, null, null, errorMessage);
    }

    /**
     * Создать ответ о неверных учетных данных.
     *
     * @return ответ с ошибкой
     */
    public static LoginResponse invalidCredentials() {
        return error("Invalid username or password");
    }
}