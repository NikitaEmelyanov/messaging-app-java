package com.telegram.clone.server.service;

import com.telegram.clone.common.dto.AuthRequestDto;
import com.telegram.clone.common.dto.AuthResponseDto;

/**
 * Сервис аутентификации пользователей.
 * Отвечает за проверку учетных данных и управление сессиями.
 */
public interface AuthService {

    /**
     * Аутентификация пользователя.
     *
     * @param request запрос с учетными данными
     * @return ответ с результатом аутентификации
     */
    AuthResponseDto authenticate(AuthRequestDto request);

    /**
     * Валидация токена сессии.
     *
     * @param token токен сессии
     * @return true если токен валиден
     */
    boolean validateToken(String token);

    /**
     * Инвалидация сессии пользователя.
     *
     * @param username имя пользователя
     */
    void invalidateSession(String username);

    /**
     * Получить имя пользователя по токену.
     *
     * @param token токен сессии
     * @return имя пользователя или null
     */
    String getUsernameByToken(String token);
}