package com.telegram.clone.server.service.impl;

import com.telegram.clone.server.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Реализация сервиса управления пользовательскими сессиями.
 */
@Slf4j
@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Set<String> onlineUsers = new CopyOnWriteArraySet<>();

    /**
     * Добавить новую сессию пользователя.
     *
     * @param username имя пользователя
     * @param sessionId идентификатор WebSocket сессии
     */
    @Override
    public void addSession(String username, String sessionId) {
        userSessions.put(username, sessionId);
        onlineUsers.add(username);
        log.info("Session added for user: {}, sessionId: {}", username, sessionId);
        log.debug("Online users count: {}", onlineUsers.size());
    }

    /**
     * Удалить сессию пользователя.
     *
     * @param username имя пользователя
     */
    @Override
    public void removeSession(String username) {
        userSessions.remove(username);
        onlineUsers.remove(username);
        log.info("Session removed for user: {}", username);
        log.debug("Online users count: {}", onlineUsers.size());
    }

    /**
     * Проверить, находится ли пользователь в сети.
     *
     * @param username имя пользователя
     * @return true если пользователь онлайн
     */
    @Override
    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    /**
     * Получить список всех онлайн пользователей.
     *
     * @return множество имен онлайн пользователей
     */
    @Override
    public Set<String> getOnlineUsers() {
        return Set.copyOf(onlineUsers);
    }

    /**
     * Получить количество активных пользователей.
     *
     * @return количество онлайн пользователей
     */
    @Override
    public int getActiveUsersCount() {
        return onlineUsers.size();
    }

    /**
     * Получить идентификатор сессии пользователя.
     *
     * @param username имя пользователя
     * @return идентификатор WebSocket сессии
     */
    @Override
    public String getSessionId(String username) {
        return userSessions.get(username);
    }
}