package com.telegram.clone.server.repository.impl;

import com.telegram.clone.server.model.UserSession;
import com.telegram.clone.server.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория сессий.
 * Хранит сессии пользователей в ConcurrentHashMap.
 */
@Slf4j
@Repository
public class InMemorySessionRepository implements SessionRepository {

    private final Map<String, UserSession> sessionsByToken = new ConcurrentHashMap<>();
    private final Map<String, UserSession> sessionsByUsername = new ConcurrentHashMap<>();

    /**
     * Создать новую сессию.
     */
    @Override
    public UserSession createSession(UserSession session) {
        sessionsByToken.put(session.getToken(), session);
        sessionsByUsername.put(session.getUsername(), session);
        log.debug("Session created for user: {}", session.getUsername());
        return session;
    }

    /**
     * Найти сессию по токену.
     */
    @Override
    public Optional<UserSession> findByToken(String token) {
        return Optional.ofNullable(sessionsByToken.get(token));
    }

    /**
     * Найти сессию по имени пользователя.
     */
    @Override
    public Optional<UserSession> findByUsername(String username) {
        return Optional.ofNullable(sessionsByUsername.get(username));
    }

    /**
     * Удалить сессию по токену.
     */
    @Override
    public boolean deleteByToken(String token) {
        UserSession session = sessionsByToken.remove(token);
        if (session != null) {
            sessionsByUsername.remove(session.getUsername());
            log.debug("Session deleted by token for user: {}", session.getUsername());
            return true;
        }
        return false;
    }

    /**
     * Удалить сессию по имени пользователя.
     */
    @Override
    public boolean deleteByUsername(String username) {
        UserSession session = sessionsByUsername.remove(username);
        if (session != null) {
            sessionsByToken.remove(session.getToken());
            log.debug("Session deleted by username: {}", username);
            return true;
        }
        return false;
    }

    /**
     * Получить все активные сессии.
     */
    @Override
    public List<UserSession> findAllActive() {
        return sessionsByToken.values().stream()
            .filter(UserSession::isActive)
            .collect(Collectors.toList());
    }

    /**
     * Получить количество активных сессий.
     */
    @Override
    public long countActive() {
        return sessionsByToken.values().stream()
            .filter(UserSession::isActive)
            .count();
    }

    /**
     * Обновить время последней активности сессии.
     */
    @Override
    public UserSession updateLastActivity(String token) {
        UserSession session = sessionsByToken.get(token);
        if (session != null) {
            session.updateLastActivity();
            log.debug("Last activity updated for session: {}", token);
        }
        return session;
    }

    /**
     * Инвалидировать просроченные сессии.
     */
    @Override
    public int invalidateExpiredSessions() {
        List<String> expiredTokens = sessionsByToken.values().stream()
            .filter(session -> !session.isActive())
            .map(UserSession::getToken)
            .collect(Collectors.toList());

        expiredTokens.forEach(this::deleteByToken);

        if (!expiredTokens.isEmpty()) {
            log.info("Invalidated {} expired sessions", expiredTokens.size());
        }

        return expiredTokens.size();
    }

    /**
     * Проверить, активна ли сессия.
     */
    @Override
    public boolean isActive(String token) {
        UserSession session = sessionsByToken.get(token);
        return session != null && session.isActive();
    }

    /**
     * Получить все сессии, истекшие до указанного времени.
     */
    @Override
    public List<UserSession> findExpiredSessions(LocalDateTime time) {
        return sessionsByToken.values().stream()
            .filter(session -> session.getExpiresAt().isBefore(time))
            .collect(Collectors.toList());
    }
}