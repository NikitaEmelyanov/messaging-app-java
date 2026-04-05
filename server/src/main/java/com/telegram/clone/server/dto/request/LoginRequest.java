package com.telegram.clone.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO запроса на вход в систему.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 */
@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequest(

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Уникальное имя пользователя",
        example = "alice",
        minLength = 3,
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
    @Schema(description = "Пароль пользователя",
        example = "pass123",
        minLength = 4,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "password")
    String password

) {
    /**
     * Компактный конструктор с нормализацией данных.
     */
    public LoginRequest {
        if (username != null) {
            username = username.trim().toLowerCase();
        }
    }

    /**
     * Проверить валидность запроса.
     *
     * @return true если запрос валиден
     */
    public boolean isValid() {
        return username != null && !username.isBlank() &&
               password != null && !password.isBlank();
    }

    @Override
    public String toString() {
        return "LoginRequest{username='" + username + "'}";
    }
}