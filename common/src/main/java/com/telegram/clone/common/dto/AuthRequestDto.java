package com.telegram.clone.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для запроса аутентификации пользователя.
 * Содержит учетные данные для входа в систему.
 *
 * @param username имя пользователя (логин)
 * @param password пароль пользователя
 */
@Schema(description = "DTO запроса аутентификации")
public record AuthRequestDto(

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Schema(description = "Уникальное имя пользователя",
        example = "alice",
        minLength = 3,
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, max = 100, message = "Пароль должен быть от 4 до 100 символов")
    @Schema(description = "Пароль пользователя",
        example = "pass123",
        minLength = 4,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "password")
    String password

) {

    /**
     * Компактный конструктор с валидацией
     */
    public AuthRequestDto {
        if (username != null) {
            username = username.trim().toLowerCase();
        }
    }

    /**
     * Проверка валидности учетных данных
     *
     * @return true если имя и пароль не пустые
     */
    public boolean isValid() {
        return username != null && !username.isBlank() &&
               password != null && !password.isBlank();
    }

    @Override
    public String toString() {
        return "AuthRequestDto{username='" + username + "'}";
    }
}