package com.telegram.clone.server.service;

import java.util.Set;

/**
 * Сервис управления пользовательскими сессиями.
 * Отслеживает активные сессии и статусы пользователей.
 */
public interface UserSessionService {

    /**
     * Добавить новую сессию пользователя.
     *
     * @param username имя пользователя
     * @param sessionId идентификатор WebSocket сессии
     */
    void addSession(String username, String sessionId);

    /**
     * Удалить сессию пользователя.
     *
     * @param username имя пользователя
     */
    void removeSession(String username);

    /**
     * Проверить, находится ли пользователь в сети.
     *
     * @param username имя пользователя
     * @return true если пользователь онлайн
     */
    boolean isUserOnline(String username);

    /**
     * Получить список всех онлайн пользователей.
     *
     * @return множество имен онлайн пользователей
     */
    Set<String> getOnlineUsers();

    /**
     * Получить количество активных пользователей.
     *
     * @return количество онлайн пользователей
     */
    int getActiveUsersCount();

    /**
     * Получить идентификатор сессии пользователя.
     *
     * @param username имя пользователя
     * @return идентификатор WebSocket сессии
     */
    String getSessionId(String username);
}