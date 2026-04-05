package com.telegram.clone.client.service;

import com.telegram.clone.client.model.ClientUser;

/**
 * Сервис аутентификации клиента.
 * Отвечает за вход в систему и управление сессией.
 */
public interface IAuthService {

    /**
     * Вход в систему.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return пользователь с токеном или null при ошибке
     */
    ClientUser login(String username, String password);

    /**
     * Выход из системы.
     *
     * @param token токен сессии
     */
    void logout(String token);

    /**
     * Проверка валидности токена.
     *
     * @param token токен для проверки
     * @return true если токен валиден
     */
    boolean validateToken(String token);
}